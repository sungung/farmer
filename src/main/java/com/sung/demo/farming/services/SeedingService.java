package com.sung.demo.farming.services;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.sung.demo.farming.Seed;
import com.sung.demo.farming.Seed.Season;
import com.sung.demo.farming.Seed.Tool;

public class SeedingService implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Seed seed = new Seed();
		seed.setSeedingSeason((Season)execution.getVariable("season"));
		seed.setRequiredMulch((boolean)execution.getVariable("mulch"));
		seed.setMaturityDays((int)execution.getVariable("maturityDays"));
		seed.setHavestingMethod((Tool)execution.getVariable("havestMethod"));
		execution.setVariable("seed", seed);
	}
}
