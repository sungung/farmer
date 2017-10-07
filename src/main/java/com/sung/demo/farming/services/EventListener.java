package com.sung.demo.farming.services;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListener implements ActivitiEventListener  {
	
	private final static Logger logger = LoggerFactory.getLogger(EventListener.class);
	
	@Override
	public void onEvent(ActivitiEvent event) {
		switch(event.getType()) {
			case PROCESS_CANCELLED:
				logger.info("PROCESS_CANCELLED: "+event.toString());
				break;
			case PROCESS_COMPLETED:
				logger.info("PROCESS_COMPLETED: "+event.toString());
				break;
			case TASK_ASSIGNED:
				logger.info("TASK_ASSIGNED: "+event.toString());
				break;
			case TASK_CREATED:
				logger.info("TASK_CREATED: "+event.toString());
				break;
			default:
				break;
		}
	}

	@Override
	public boolean isFailOnException() {
		// TODO Auto-generated method stub
		return false;
	}

}
