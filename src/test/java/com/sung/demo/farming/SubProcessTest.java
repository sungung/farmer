package com.sung.demo.farming;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.subethamail.wiser.Wiser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SubProcessTest {
	
	@Autowired
	private ProcessEngine processEngine;	

	@Autowired
	RuntimeService runtimeService;	
	
	@Autowired
	TaskService taskService;	
	
    private Wiser wiser;

    @Before
    public void setup() {
        wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();
    }	
    
    @After
    public void cleanup() {
        wiser.stop();
    }  
		
	@Test
	public void test_watering_in_time(){
	
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", false);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("escalate_seeding", variables);		
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		taskService.complete(seeding.getId());
		
		// next task shall be watering because we do not need mulching
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		// complete watering within a second !!
		assertEquals("Watering", watering.getName());
		taskService.complete(watering.getId());
		
		Task harvest = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Watering", watering.getName());
		taskService.complete(harvest.getId());
		
		assertEquals(0L, taskService.createTaskQuery().processInstanceId(p.getId()).count());
	}
	
	@Test
	public void test_escalate_auto_watering(){
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", false);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("escalate_seeding", variables);		
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		taskService.complete(seeding.getId());
		
		// next task shall be watering because we do not need mulching
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		// wait a second
		try {
			//waitForTasksToExpire();
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// watering shall be done automatically then now task should be harvest
		Task harvesting = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Harvest", harvesting.getName());
		
		taskService.complete(harvesting.getId());
		
		assertEquals(0L, taskService.createTaskQuery().processInstanceId(p.getId()).count());
		
	}
	
	private void waitForTasksToExpire() throws InterruptedException {
		boolean finished = false;
		int nrOfSleeps = 0;
		ManagementService managementService = processEngine.getManagementService();
		while (!finished){
			long jobCount = managementService.createJobQuery().count();
			if (jobCount == 0){
				finished = true;
			} else if (nrOfSleeps < 50) {
				nrOfSleeps++;
				Thread.sleep(1000L);
			} else {
				finished = true;
			}
		}
	}	
}
