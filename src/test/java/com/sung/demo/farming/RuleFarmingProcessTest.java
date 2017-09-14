package com.sung.demo.farming;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

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
public class RuleFarmingProcessTest {
	
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
	public void test_winter_seeding(){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.WINTER);
		variables.put("mulch", true);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		ProcessInstance p = runtimeService.startProcessInstanceByKey("rule_farming", variables);
		// Winter is not allowed to sow, then you will get alert email
		assertEquals(1, wiser.getMessages().size());
	}
	
	@Test
	public void test_spring_seeding(){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", true);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		ProcessInstance p = runtimeService.startProcessInstanceByKey("rule_farming", variables);
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Seeding", seeding.getName());
	}
}
