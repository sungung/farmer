package com.sung.demo.farming;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.deploy.Deployer;
import org.activiti.engine.impl.rules.RulesDeployer;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.sung.demo.farming.services.EventListener;

@SpringBootApplication
public class FarmingApplication {
	
	private final static Logger logger = LoggerFactory.getLogger(FarmingApplication.class);
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FarmingApplication.class, args);
	}
	
	@Autowired
	private ProcessEngineFactoryBean processEngine;
	
	/***
	 * @PostContruct may not be perfect place to modify process engine.
	 * As default, ActivitiProcessEngine does not come with Drool resource deployer,
	 * to use rule business task we need to modify engine.
	 */
	@PostConstruct
	void injectDroolsResources(){
		if (processEngine.getProcessEngineConfiguration().getCustomPostDeployers() == null) {
			processEngine.getProcessEngineConfiguration().setCustomPostDeployers(Arrays.asList(new Deployer[]{new RulesDeployer()}));
		} else {
			processEngine.getProcessEngineConfiguration().getCustomPostDeployers().add(new RulesDeployer());
		}
		processEngine.getProcessEngineConfiguration().setEventListeners(Arrays.asList(new ActivitiEventListener[]{new EventListener()}));
	}
	
}