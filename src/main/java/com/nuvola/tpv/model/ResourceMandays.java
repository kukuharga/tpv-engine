package com.nuvola.tpv.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class ResourceMandays implements Serializable {
	private String role;
	private String level;
	private int mandays;
	
	public ResourceMandays(String role, String level) {
		super();
		this.role = role;
		this.level = level;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceMandays other = (ResourceMandays) obj;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}
	
	
	
	

}
