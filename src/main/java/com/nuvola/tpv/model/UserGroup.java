package com.nuvola.tpv.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroup {

	@Id
	private String code;

	private String name;

	private Set<String> roles;
	

}
