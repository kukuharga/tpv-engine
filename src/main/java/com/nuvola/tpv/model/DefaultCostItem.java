package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class DefaultCostItem {
	@Id
	private String id;
	private String service;
	private String category;
	private CostItem costItem;

}
