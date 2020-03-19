package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;

import com.nuvola.tpv.model.Names.LobType;

import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class ProjectType {

	public ProjectType(LobType code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	@Id
	private LobType code;
	private String name;
}
