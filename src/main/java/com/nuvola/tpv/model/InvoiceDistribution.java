package com.nuvola.tpv.model;

import lombok.Data;

public @Data class InvoiceDistribution {
	private int term;
	private String department;
	private String service;
	private double amount; //Project Amount
	
	
}
