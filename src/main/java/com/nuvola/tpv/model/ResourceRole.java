package com.nuvola.tpv.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import lombok.Data;



public @Data class ResourceRole {
	
	

	@Id
	private String code;
	
	private String name;
	
	private double loadedCost;
	
	private double billingRate;
	
	private String level;

	@Override
	public String toString() {
		return "ResourceRole [code=" + code + ", name=" + name + ", loadedCost=" + loadedCost + ", billingRate="
				+ billingRate + ", level=" + level + "]";
	}
	

}
