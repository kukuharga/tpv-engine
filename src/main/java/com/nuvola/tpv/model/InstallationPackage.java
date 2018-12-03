package com.nuvola.tpv.model;



import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Data;

public @Data class InstallationPackage {

	
	@Id
	private String code; // Service Code
	private String description; // Description
	private String type; //Installation Type
	private String location; //Location
	private int workingDays; //Duration
	private int prepDays; //Preparation Day
	private double lrEngineer; //Loaded Rate Lead Consultant
	private double lrAssistant; //Loaded Rate Assistant
	private double brEngineer; //Billing Rate Lead Consultant
	private double brAssistant; //Billing Rate Assistant
	private int engineerCount; //#Lead Consultant
	private int assistantCount; //#Assistant
	private int minServer; //Minimum Server
	private double extraSvrCost;//Additional Server Cost
	private double docCost; //Documentation Cost
	
	
	@Transient
	public int getDurationDays(){
		return this.workingDays + this.prepDays;
	}
	
	public double getConsultantCost() {
		return (lrEngineer * engineerCount + lrAssistant * assistantCount) * workingDays;
	}	
	
	public double getConsultantBill() {
		return (brEngineer * engineerCount + brAssistant * assistantCount) * workingDays;
	}
	
	
	
	public double getInitialCost() {
		return getConsultantCost() + this.docCost;
	}
	
	public double getInitialBill() {
		return getConsultantBill() + this.docCost;
	}
	
	public String getTitle() {
		return this.description + " - " + this.type + " - "  + this.location;
	}
	
	
	

	

}
