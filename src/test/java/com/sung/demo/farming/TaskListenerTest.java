package com.sung.demo.farming;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
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
import org.subethamail.wiser.WiserMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TaskListenerTest {

	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	TaskService taskService;
	
	@Autowired
	IdentityService identityService;
	
    private Wiser wiser;
    
    @After
    public void cleanup() {
        wiser.stop();
    }    
	
	@Before
	public void setUp(){
		
		Group ssr = identityService.newGroup("ssr");
		identityService.saveGroup(ssr);
		
		User john = identityService.newUser("john");
		identityService.saveUser(john);
		
		User chris = identityService.newUser("chris");
		identityService.saveUser(chris);
		
		identityService.createMembership("john", "ssr");
		identityService.createMembership("chris", "ssr");
	
        wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();
		
	}
	
	@Test
	public void test_is_working() throws MessagingException{
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("season", Seed.Season.SPRING);
		variables.put("mulch", false);
		variables.put("harvestMethod", Seed.Tool.LABOUR);
		variables.put("maturityDays", 90);
		
		ProcessInstance p = runtimeService.startProcessInstanceByKey("process_task_listener", variables);		
		
		Task seeding = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		
		// To make wiser test work, email task should synchronised not async
		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(1, messages.size());
		
		WiserMessage message = messages.get(0);
		MimeMessage mimeMessage = message.getMimeMessage();
		assertEquals("about to Seeding", mimeMessage.getHeader("Subject", null));
		
		taskService.complete(seeding.getId());
		
		// You must see iteration of string in the console
		messages = wiser.getMessages();
		assertEquals(2, messages.size());

		Task watering = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(watering.getId());

		messages = wiser.getMessages();
		assertEquals(3, messages.size());

		Task havest = taskService.createTaskQuery().processInstanceId(p.getId()).singleResult();
		taskService.complete(havest.getId());
		
		assertEquals(0L, taskService.createTaskQuery().processInstanceId(p.getId()).count());		

	}
	
}
