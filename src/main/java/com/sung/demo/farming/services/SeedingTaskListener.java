package com.sung.demo.farming.services;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeedingTaskListener implements TaskListener {
	
	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	TaskService taskService;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		// Throwing a signal event
		runtimeService.signalEventReceived("theSignalName", delegateTask.getVariables());
	}

}
