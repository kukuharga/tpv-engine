package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

public @Data class Milestone {
	@Id
	private String code;
	private String name;

}
