package com.nuvola.tpv.model;

import java.util.Date;

import com.nuvola.tpv.model.Names.DecisionType;
import com.nuvola.tpv.model.Names.ReviewStatus;
import com.nuvola.tpv.model.Names.ReviewerType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class AssociatedProject {
	private String projectId;
	private String name;
	private String description;
	private Double cost;
	private Double revenue;
	private Double overrideRevenue;
	
	public double getMargin() {
		return this.revenue - this.cost;

	}

	public double getMarginPercent() {
		if (this.revenue == 0) return 0d;
		return getMargin() / this.revenue;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociatedProject other = (AssociatedProject) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	
	

}
