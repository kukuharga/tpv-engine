package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class TrainingPackage {

	@Id
	private String code; // Service Code
	private String description; // Description
	private String type; // Installation Type
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
	private double refreshmentCost; // Lunch & Snack Per Participant Per Day
	private double serverCost; // Server Cost Per Participant
	private double localTransportCost; // Local Transport Cost
	private double courseMaterialFee; // Course Material Fee
	private String remarks; //Remarks (TextArea)


	public int getDurationDays() {
		return this.workingDays + this.prepDays;
	}

	public double getConsultantCost() {
		return (lrInstructor * instructorCount + lrAssistant * assistantCount) * workingDays;
	}

	public double getConsultantBill() {
		return (brInstructor * instructorCount + brAssistant * assistantCount) * workingDays;
	}

	public double getTotalRefreshmentCost() {
		return (refreshmentCost * minParticipant) * workingDays;
	}

	public double getLeadInstructorCost() {
		return lrInstructor * instructorCount;
	}

	public double getAssistantCost() {
		return lrAssistant * assistantCount;
	}

	public double getLeadInstructorBill() {
		return brInstructor * instructorCount;
	}

	public double getAssistantBill() {
		return brAssistant * assistantCount;
	}

	public int getTotalInstructorCount() {
		return instructorCount + assistantCount;
	}

	public double getInstructorRunCost() {
		return (getLeadInstructorCost() + getAssistantCost() + localTransportCost * getTotalInstructorCount())
				* this.getDurationDays();
	}

	public double getInstructorRunBill() {
		return (getLeadInstructorBill() + getAssistantBill() + localTransportCost * getTotalInstructorCount())
				* getDurationDays();
	}

	public String getTitle() {
		return this.description + " - " + this.type + " - " + this.location;
	}

	public double getCostPerParticipant() {
		return (getInstructorRunCost() / this.minParticipant) + (this.refreshmentCost * this.workingDays)
				+ this.courseMaterialFee + this.serverCost;
	}

	public double getBillPerParticipant() {
		return (getInstructorRunBill() / this.minParticipant) + (this.refreshmentCost * this.workingDays)
				+ this.courseMaterialFee + this.serverCost;
	}

	public double getGMPerParticipant() {
		return getBillPerParticipant() - getCostPerParticipant();
	}

	public double getGrossMargin() {
		return getGMPerParticipant() / getBillPerParticipant();
	}
}
