package com.sung.demo.farming;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TimerMultiInstanceProcessTest {

	@Autowired
	RuntimeService runtimeService;	
	
	@Autowired
	TaskService taskService;
	
	@Test
	public void test_finish_multi_watering_in_time(){
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", false);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("timer_in_multi_instance_watering", variables);		
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		taskService.complete(seeding.getId());
		
		List<Task> waterings = taskService.createTaskQuery().processInstanceId(p.getId()).list();
		
		// as default, there are three assignee for watering
		assertEquals(3, waterings.size());
		
		waterings.forEach(w -> {
			taskService.complete(w.getId());
		});
		
		Task harvest = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		// all three watering completed in time, so now harvesting
		assertEquals("Harvest", harvest.getName());
		
		taskService.complete(harvest.getId());
	}
	
	/***
	 * watering won't be finished in time, so it will throw exception when
	 * try to complete time outed task
	 */
	@Test(expected = ActivitiObjectNotFoundException.class)
	public void test_pending_completion_watering(){

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", false);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("timer_in_multi_instance_watering", variables);		
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		taskService.complete(seeding.getId());
		
		List<Task> waterings = taskService.createTaskQuery().processInstanceId(p.getId()).list();
		
		// as default, there are three assignee for watering
		assertEquals(3, waterings.size());
		
		for (int i=0; i<3; i++) {
			
			taskService.complete(waterings.get(i).getId());
			
			// make lazy tasking, so it will trigger timeout
			try {
				Thread.sleep(10000L);
			} catch(InterruptedException e){
				e.printStackTrace();
			}			
		}
		
		
	}
}
