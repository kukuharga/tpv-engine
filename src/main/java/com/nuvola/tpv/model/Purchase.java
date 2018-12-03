package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Data;



public @Data class Purchase {

	@Id
	private String id;

	private String name;

	private String type;

	private String supplier;

	private Integer quantity;

	private double priceBuy;

	private double priceSell;
	
	private String projectId;

	private String department;
	
	@Transient
	public Double getMargin() {
		return this.priceSell - this.priceBuy;
	}
	
	public Double getMarginPercent() {
		if(this.priceSell == 0) return 0d;
		return getMargin() / this.priceSell;
	}

}
