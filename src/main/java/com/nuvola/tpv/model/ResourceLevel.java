package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ResourceLevel {
	
	@Id
	private String code;
	private String name;
	
}
