package com.nuvola.tpv.model;



import lombok.Data;

public @Data class CostItem {
	private String itemName;
	private int quantity;
	private String uom;
	private double amount;
	
	
	public double getTotal() {
		return this.quantity * this.amount;
	}
}
