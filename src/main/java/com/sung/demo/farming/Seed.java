package com.sung.demo.farming;

import java.io.Serializable;

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
	private Tool havestingMethod;
	private int maturityDays;
	private boolean okayToSeeding;
	
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
	public Tool getHavestingMethod() {
		return havestingMethod;
	}
	public void setHavestingMethod(Tool havestingMethod) {
		this.havestingMethod = havestingMethod;
	}
	public int getMaturityDays() {
		return maturityDays;
	}
	public void setMaturityDays(int maturityDays) {
		this.maturityDays = maturityDays;
	}
	
	
}
