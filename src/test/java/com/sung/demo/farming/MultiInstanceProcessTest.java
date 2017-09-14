package com.sung.demo.farming;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
public class MultiInstanceProcessTest {
	
	@Autowired
	RuntimeService runtimeService;	
	
	@Autowired
	TaskService taskService;
	
	@Test
	public void test_multi_instance_watering(){
	
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", false);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("multi_watering", variables);		
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		taskService.complete(seeding.getId());
		
		// You must see iteration of string in the console
		
		Task harvest = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Harvest", harvest.getName());
		
		taskService.complete(harvest.getId());
		
		assertEquals(0L, taskService.createTaskQuery().processInstanceId(p.getId()).count());
	}	
	
}
