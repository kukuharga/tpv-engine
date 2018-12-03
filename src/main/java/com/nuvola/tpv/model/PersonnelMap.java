package com.nuvola.tpv.model;



import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class PersonnelMap {
	private String roleCd;
	private String roleNm;
	private String rscLevel;
	private int count;
	
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonnelMap other = (PersonnelMap) obj;
		if (roleCd == null) {
			if (other.roleCd != null)
				return false;
		} else if (!roleCd.equals(other.roleCd))
			return false;
		if (rscLevel == null) {
			if (other.rscLevel != null)
				return false;
		} else if (!rscLevel.equals(other.rscLevel))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleCd == null) ? 0 : roleCd.hashCode());
		result = prime * result + ((rscLevel == null) ? 0 : rscLevel.hashCode());
		return result;
	}
	
	public PersonnelMap(String roleCd, String rscLevel) {
		super();
		this.roleCd = roleCd;
		this.rscLevel = rscLevel;
	}

	@Override
	public String toString() {
		return "PersonnelMap [roleCd=" + roleCd + ", roleNm=" + roleNm + ", rscLevel=" + rscLevel + ", count=" + count
				+ "]";
	}
	
	
	
	
	
}
