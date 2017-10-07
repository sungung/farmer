package com.sung.demo.farming;

import static org.junit.Assert.*;

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
	
	/**
	 * Testing simple farming process which is
	 * seeding -> watering -> harvest
	 */
	@Test
	public void test_complete_farming(){
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		
		// complete seeding
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(seeding.getId());
		
		// now task shall be watering
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Watering", watering.getName());
		
		// complete watering
		taskService.complete(watering.getId());
		
		// current task is harvesting
		Task harvesting = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Harvest", harvesting.getName());		
		// and we shall have one pending task to finish farming
		assertEquals(1L, taskService.createTaskQuery().processInstanceId(p.getId()).count());
		
		// complete last task
		taskService.complete(harvesting.getId());		
		// then there will be no more task left.
		assertEquals(0L, taskService.createTaskQuery().processInstanceId(p.getId()).count());
	}
	
	@Test
	public void test_sam_is_assignee_of_watering(){

		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		
		// complete seeding
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(seeding.getId());
		
		// now task shall be watering
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertEquals("Watering", watering.getName());
		// is Sam assignee of watering task
		assertEquals("Sam", watering.getAssignee());
		
		// query task assigned to Sam
		assertTrue(taskService.createTaskQuery().taskAssignee("Sam").count() > 0L);
		
		taskService.complete(watering.getId());
		
		Task harvesting = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(harvesting.getId());
	}
	
	@Test
	public void assign_phil_to_seeding(){
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		// assign Phil to seeding task
		taskService.setAssignee(seeding.getId(), "phil");
		
		// we must get single task belongs to Phil which is seeding task
		assertTrue(taskService.createTaskQuery().taskAssignee("phil").count() > 0L);
		// also can get same result from involved user query
		assertTrue(taskService.createTaskQuery().taskInvolvedUser("phil").count() > 0L);
		
		taskService.complete(seeding.getId());
	}
	
	@Test
	public void claim_by_phil(){
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		// Phil claims seeding task
		taskService.claim(seeding.getId(), "phil");
		// Phil now becoming assignee of seeding task
		assertEquals(seeding.getId(), taskService.createTaskQuery().taskAssignee("phil").singleResult().getId());
		// complete seeding task
		taskService.complete(seeding.getId());
		// Now Phil should not have any task
		assertEquals(0L, taskService.createTaskQuery().taskAssignee("phil").count());
		
	}
	
	@Test
	public void cancel_farming(){
		ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleFarming");
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(seeding.getId());
		// delete process instance will cancel process and dispatch PROCESS_CANCELLED event
		runtimeService.deleteProcessInstance(p.getId(), "Abort!!");
		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		assertNull(watering);
	}
		
}
