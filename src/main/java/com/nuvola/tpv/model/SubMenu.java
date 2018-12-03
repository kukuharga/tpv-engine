package com.nuvola.tpv.model;



import java.util.Collection;

import lombok.Data;



public @Data class SubMenu {
	
	private String code;
	private String name;
	private String page;
	private String icon;
	private String path;
	@Override
	public String toString() {
		return "SubMenu [code=" + code + ", name=" + name + ", page=" + page + ", icon=" + icon + ", path=" + path
				+ "]";
	}
	
}
