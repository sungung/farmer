package com.sung.demo.farming.services;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FarmingTaskListener implements TaskListener {
	
	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	TaskService taskService;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		// Throwing a signal event
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.putAll(delegateTask.getVariables());
		variables.put("currentTask", delegateTask.getName());
		runtimeService.signalEventReceived("theSignalName", variables);
	}

}
