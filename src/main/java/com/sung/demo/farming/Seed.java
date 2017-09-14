package com.sung.demo.farming;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Seed implements Serializable{
	
	public static enum Season {
		SPRING,
		SUMMER,
		AUTUMN,
		WINTER		
	}
	
	public static enum Tool {
		MACHINE,
		CATTLE,
		LABOUR
	}
	
	private Season seedingSeason;
	private boolean requiredMulch;
	private Tool harvestingMethod;
	private int maturityDays;
	private boolean okayToSeeding;
	private List<String> team = Arrays.asList(new String[]{"Sam", "Phil", "Sarah"});
	
	public Season getSeedingSeason() {
		return seedingSeason;
	}
	public void setSeedingSeason(Season seedingSeason) {
		this.seedingSeason = seedingSeason;
	}

	public boolean isRequiredMulch() {
		return requiredMulch;
	}
	public void setRequiredMulch(boolean requiredMulch) {
		this.requiredMulch = requiredMulch;
	}
	public boolean isOkayToSeeding() {
		return okayToSeeding;
	}
	public void setOkayToSeeding(boolean okayToSeeding) {
		this.okayToSeeding = okayToSeeding;
	}
	public Tool getHarvestingMethod() {
		return harvestingMethod;
	}
	public void setHarvestingMethod(Tool harvestingMethod) {
		this.harvestingMethod = harvestingMethod;
	}
	public int getMaturityDays() {
		return maturityDays;
	}
	public void setMaturityDays(int maturityDays) {
		this.maturityDays = maturityDays;
	}
	public List<String> getTeam() {
		return team;
	}
	public void setTeam(List<String> team) {
		this.team = team;
	}

}
