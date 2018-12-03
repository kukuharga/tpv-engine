package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class Activity {
	@Id
	private String code;
	private String name;
	private String group;
	private String projectType;
	
	
}
