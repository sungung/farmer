package com.sung.demo.farming;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.activiti.engine.ProcessEngine;
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
public class SimpleFarmingProcessTest {
	
	@Autowired
	private ProcessEngine processEngine;
	
	@Autowired
	RuntimeService runtimeService;	
	
	@Autowired
	TaskService taskService;	
	
	@Test
	public void is_ok_configuration(){
		assertNotNull(processEngine);
	}
	
	@Test
	public void is_first_task_seeding(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task t = taskService.createTaskQuery().processInstanceId(p.getId()).list().get(0);
		assertEquals("Seeding", t.getName());
	}
	
	@Test
	public void is_last_task_havesting(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(seeding.getId());
		assertEquals("Watering", taskService.createTaskQuery().processInstanceId(p.getId()).singleResult().getName());
		taskService.complete(taskService.createTaskQuery().processInstanceId(p.getId()).singleResult().getId());
		assertEquals("Havesting", taskService.createTaskQuery().processInstanceId(p.getId()).singleResult().getName());
	}
	
	@Test
	public void is_sam_owner_of_watering(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(seeding.getId());
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Sam", watering.getAssignee());
	}
	
	@Test
	public void sam_must_have_watering_task(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		taskService.complete(taskService.createTaskQuery().processInstanceId(p.getId()).singleResult().getId());		
		assertEquals(1,taskService.createTaskQuery().taskAssignee("Sam").list().size());
	}
	
	@Test
	public void add_phil_to_seedtask_candidate(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.addCandidateUser(seeding.getId(), "phil");
		Task philtask = taskService.createTaskQuery().taskCandidateUser("phil").singleResult();
		assertEquals(seeding.getId(), philtask.getId());
	}
	
	@Test
	public void assign_phil_to_seeding(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.setAssignee(seeding.getId(), "phil");
		assertEquals(seeding.getId(), taskService.createTaskQuery().taskAssignee("phil").singleResult().getId());
		assertEquals(seeding.getId(), taskService.createTaskQuery().taskInvolvedUser("phil").singleResult().getId());
	}
	
	@Test
	public void claim_by_phil(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.addCandidateUser(seeding.getId(), "phil");
		taskService.claim(seeding.getId(), "phil");
		assertEquals(seeding.getId(), taskService.createTaskQuery().taskAssignee("phil").singleResult().getId());
		
		taskService.unclaim(seeding.getId());
		taskService.claim(seeding.getId(), "sam");
		assertEquals(seeding.getId(), taskService.createTaskQuery().taskAssignee("sam").singleResult().getId());
		
		taskService.complete(seeding.getId());
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Sam", watering.getAssignee());
		taskService.unclaim(watering.getId());
		taskService.claim(watering.getId(), "phil");
		assertEquals(watering.getId(), taskService.createTaskQuery().taskAssignee("phil").singleResult().getId());
	}
	
	@Test
	public void dummy(){
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.get(Calendar.YEAR) + "|" +cal.get(Calendar.MONTH) + "|" +cal.get(Calendar.DAY_OF_MONTH));
	}
	
}
