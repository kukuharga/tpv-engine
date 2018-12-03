package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class Personnel {
	@Id
	private String code;
	private String roleCd;
	private String roleNm;
	private String rscLevel;
	private double billingRt;
	private double loadedCst;
	
	
	
}
