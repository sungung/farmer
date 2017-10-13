package com.sung.demo.farming;

import static org.junit.Assert.*;

import java.util.Arrays;
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

import com.sung.demo.farming.Seed.Season;
import com.sung.demo.farming.Seed.Tool;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PojoAsProcessVarTest {

	@Autowired
	RuntimeService runtimeService;	
	
	@Autowired
	TaskService taskService;
	
	@Test
	public void isWorking(){
		
		Seed seed = new Seed();
		seed.setHarvestingMethod(Tool.LABOUR);
		seed.setMaturityDays(100);
		seed.setOkayToSeeding(true);
		seed.setRequiredMulch(true);
		seed.setSeedingSeason(Season.SPRING);
		seed.setTeam(Arrays.asList(new String[]{"Sung","Phil","Wang"}));
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("seed", seed);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("pojo_var_process", variables);
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		taskService.complete(seeding.getId());
		
		Task harvesting = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		assertEquals("Harvest", harvesting.getName());
		
		assertEquals(0L, taskService.createTaskQuery().processInstanceId(p.getId()).count());
	}
}
