package com.nuvola.tpv.model;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class Training {

	@Id
	private String code;
	private String projectId; // Project ID
	private String trainingPackage; // Training Service Code
	private String type; // Training Type
	private String location; // Location
	private int workingDays; // Duration
	private int prepDays; // Preparation Day
	private double lrInstructor; // Loaded Rate Lead Instructor
	private double lrAssistant; // Loaded Rate Assistant
	private double brInstructor; // Billing Rate Lead Instructor
	private double brAssistant; // Billing Rate Assistant
	private int instructorCount; // #Lead Instructor
	private int assistantCount; // #Assistant
	private int minParticipant; // Minimum Participant
	private double participantCost;// Cost Per Participant (Override)
	private int participantCount; // #Participant
	private String title;
	// private double sellingPrice;
	private Collection<CostItem> costItems;
	private Collection<String> requiredItems;
	private double courseMaterialFee; // Course Material Fee
	private double serverCost; // Server Cost Per Participant
	private double refreshmentCost; // Lunch & Snack Per Participant Per Day
	private double localTransportCost; // Local Transport Cost

	public int getDurationDays() {
		return this.workingDays + this.prepDays;
	}

	public double getConsultantCost() {
		return (lrInstructor * instructorCount + lrAssistant * assistantCount);
	}

	public double getConsultantBill() {
		return (brInstructor * instructorCount + brAssistant * assistantCount);
	}

	public double getTotalRefreshmentCost() {
		return (refreshmentCost * minParticipant) * workingDays;
	}



	public int getTotalConsultantCount() {
		return instructorCount + assistantCount;
	}

	public double getConsultantRunCost() {
		return (getConsultantCost() + localTransportCost * getTotalConsultantCount())
				* (this.getDurationDays() + this.prepDays);
	}

	public double getConsultantRunBill() {
		return (getConsultantBill() + localTransportCost * getTotalConsultantCount())
				* (this.getDurationDays() + this.prepDays);
	}

	public double getItemsCost() {
		return this.costItems.stream().collect(Collectors.summingDouble(n -> n.getTotal()));
	}

	public double getCostPerParticipant() {
		return (getConsultantRunCost() / this.participantCount) + (this.refreshmentCost * this.workingDays)
				+ this.courseMaterialFee + this.serverCost + (getItemsCost() / this.participantCount);
	}
	
	public double getBillPerParticipant() {
		return (getConsultantRunBill() / this.participantCount) + (this.refreshmentCost * this.workingDays)
				+ this.courseMaterialFee + this.serverCost + (getItemsCost() / this.participantCount);
	}

	public double getGMPerParticipant() {
		return getSellingPricePerParticipant() - getCostPerParticipant();
	}

	public double getGMPercentage() {
		if(getSellingPricePerParticipant() == 0) return 0d;
		return getGMPerParticipant() / getSellingPricePerParticipant();
	}
	
	private double getSellingPricePerParticipant() {
		return (this.participantCost == 0) ? getBillPerParticipant(): this.participantCost;
	}

	
	public double getSellingPrice() {
		return getSellingPricePerParticipant() * this.participantCount;
	}
	
	public double getFinalCost() {
		return getCostPerParticipant() * this.participantCount;
	}

}
