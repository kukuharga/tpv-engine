package com.nuvola.tpv.model;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import lombok.Data;



public @Data class Menu {
	
	@Id
	private String code;
	private String name;
	private String page;
	private String icon;
	private String path;
	private Collection<SubMenu> subMenu;
	
	
}

