package com.nuvola.tpv.model;




import lombok.Data;

public @Data class MandaysSummary {

	private String roleCd;
	private String roleNm;
	private String rscLevel;
	private double billingRt;
	private double loadedCst;
	private int count;
	private int mandays;
	private float rscUtil;
	
	public Double getTotalBilling() {
		return this.billingRt * mandays;
	}
	
	public Double getTotalCost() {
		return this.loadedCst * mandays;
	}
	

}
