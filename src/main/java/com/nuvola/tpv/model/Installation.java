package com.nuvola.tpv.model;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class Installation {

	@Id
	private String code;
	private String projectId;
	private String instPackage; // Installation Service Code
	private String type; // Installation Type
	private String location; // Location
	private int workingDays; // Duration
	private int prepDays; // Preparation Day
	private double lrEngineer; // Loaded Rate Lead Consultant
	private double lrAssistant; // Loaded Rate Assistant
	private double brEngineer; // Billing Rate Lead Consultant
	private double brAssistant; // Billing Rate Assistant
	private int engineerCount; // #Lead Consultant
	private int assistantCount; // #Assistant
	private int minServer; // Minimum Server
	private double extraSvrCost;// Additional Server Cost
	private double docCost; // Documentation Cost
	private int svrCount;
	private String title;
	private double sellingPrice;
	private Collection<CostItem> costItems;
	private Collection<String> requiredItems;

	public double getConsultantCost() {
		return (lrEngineer * engineerCount + lrAssistant * assistantCount) * workingDays;
	}

	public double getConsultantBill() {
		return (brEngineer * engineerCount + brAssistant * assistantCount) * workingDays;
	}

	public double getItemsCost() {
		return this.costItems.stream().collect(Collectors.summingDouble(n -> n.getTotal()));
	}

	public double getInitialCost() {
		return getConsultantCost() + this.docCost;
	}

	public double getInitialBill() {
		return getConsultantBill() + this.docCost;
	}

	public double getFinalCost() {
		double additionalSvrChg = (this.svrCount - this.minServer) * extraSvrCost;
		return additionalSvrChg + getItemsCost() + getInitialCost();
	}

	public double getFinalBill() {
		double additionalSvrChg = (this.svrCount - this.minServer) * extraSvrCost;
		return additionalSvrChg + getItemsCost() + getInitialBill();
	}

	public double getSellingPrice() {
		return this.sellingPrice == 0 ? getFinalBill() : this.sellingPrice;
	}

	public double getGrossMargin() {
		return getSellingPrice() - getFinalCost();
	}

	public double getGMPercent() {
		if(getSellingPrice() == 0) return 0d;
		return getGrossMargin() / getSellingPrice();
	}

}
