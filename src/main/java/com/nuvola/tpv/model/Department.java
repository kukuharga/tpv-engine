package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;




public @Data @NoArgsConstructor class Department {
	
	
	
	public Department(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}


	@Id
	private String code;
	
	
	private String name;

	

	
}
