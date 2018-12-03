package com.nuvola.tpv.model;

import lombok.Data;

public @Data class DepartmentRevenue {
	private String chargeCode;
	private String salesPerson;
	private String presalesPerson;
	private String department;
	private String service;
	private double revenue; //Project Amount
	//private String projectId;
	private String projectNm;
	private String projectCode;
	
	
	
}
