package com.nuvola.tpv.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import com.nuvola.tpv.model.Names.DocStatus;
import com.nuvola.tpv.model.Names.LobType;


import lombok.Data;

public @Data class Project extends Auditable<String>{

	@Id
	private String id;

	private String name;

	private String code;

	private String clientName;

	private Date startDate;

	private Integer weekDuration;

	private String sales;

	private String department;

	private String projectGroup;

	private LobType service;

	private String salesLead;

	private String presales1;

	private String presales2;

	private String pmoDelivery;

	private String solArchitect;

	private String description;

	private String poCurrency = "IDR";

	private String projectType;

	private String poNumber;

	private String leadStage;

	private Date closeDate;

	private Date kickOffDate;

	private double revenue;
	
	private double ovrRevenue;

	private double cost;
	
	private double outOfPocket;
	
	private double subProjectsCost;
	
	private double subProjectsRevenue;
	
	public double getMainCost() {
		return this.cost + this.outOfPocket;
	}
	
	public double getTotalCost() {
		return getMainCost() + this.subProjectsCost;
	}
	
	public double getTotalRevenue() {
		return getFinalRevenue() + getSubProjectsRevenue();
	}
	
	public double getTotalMargin() {
		return getTotalRevenue() - getTotalCost();
	}
	
	public double getTotalMarginPercent() {
		if(getTotalCost() == 0) return 0d;
		return getTotalMargin() / getTotalCost();
		
	}
	
	private Set<Review>reviews;
	
	private DocStatus docStatus = DocStatus.DRAFT;
	
	private boolean locked;
	
	//private List<AssociatedProject>associatedProjects;
	
	private Set<String> associatedProjectIds = new HashSet<>();
	
	
	private boolean isSubProject;

	@Transient
	public Date getFinishDate() {
		if(this.weekDuration == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.startDate);
		cal.add(Calendar.DATE, this.weekDuration * 7);
		return cal.getTime();
	}
	
	/**
	 * Project duration in working days.
	 * @return
	 */
	public int getProjectDuration() {
		if(this.weekDuration == null) return 0;
		return this.weekDuration * 5;
	}
	
	public Double getFinalRevenue() {
		return this.ovrRevenue;
	}

	public Double getMargin() {
		return getFinalRevenue() - this.getMainCost();

	}
	
	public Double getMarginPercent() {
		if (getMainCost() == 0) return 0d;
		return getMargin() / this.getMainCost();

	}
	
	public Double getSubProjectsMargin() {
		return getSubProjectsRevenue() - getSubProjectsCost();

	}
	
	public Double getSubProjectsMarginPercent() {
		if (getSubProjectsRevenue() == 0) return 0d;
		return getSubProjectsMargin() / this.getSubProjectsRevenue();

	}
	
	public void addAllReviews(Set<Review>reviews) {
		if(this.reviews == null) 
			this.reviews = reviews;
		else this.reviews.addAll(reviews);
	}
	
	public void addReview(Review review) {
		if(this.reviews == null)
			this.reviews = new HashSet<>();
		this.reviews.add(review);
	}
	
	public void setRevenue(double revenue) {
		this.revenue = revenue;
		this.ovrRevenue = revenue;
	}


	

}
