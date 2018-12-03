package com.nuvola.tpv.model;

import java.util.Calendar;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import lombok.Data;

public @Data class Project {

	@Id
	private String id;

	private String name;

	private String code;

	private String clientName;

	private Date startDate;

	private Integer weekDuration;

	private String sales;

	private String department;

	private Date lastModified;

	private String projectGroup;

	private String service;

	private String salesLead;

	private String presales1;

	private String presales2;

	private String pmoDelivery;

	private String solArchitect;

	private String description;

	private String poCurrency="IDR";

	private String projectType;

	private String poNumber;

	private String leadStage;

	private Date closeDate;

	private Date kickOffDate;

	private double revenue;

	private double cost;
	
	private double outOfPocket;
	
	

	@Transient
	public Date getFinishDate() {
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
		return this.weekDuration * 5;
	}

	public double getMargin() {
		return this.revenue - this.cost;

	}

	public double getMarginPercent() {
		if (this.revenue == 0) return 0d;
		return getMargin() / this.revenue;

	}

}
