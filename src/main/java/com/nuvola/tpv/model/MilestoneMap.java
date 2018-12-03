package com.nuvola.tpv.model;



import lombok.Data;

public @Data class MilestoneMap {
	private String code;
	private String name;
	private int week;
	
	public String getMilestoneName() {
		return new StringBuffer().append(code).append("-").append(name).toString();
	}

}
