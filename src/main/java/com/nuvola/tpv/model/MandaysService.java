package com.nuvola.tpv.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import com.nuvola.tpv.model.Names.TpvStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class MandaysService extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = -2508492853368666125L;
	@Id
	private String code;
	private String projectId;
	private Set<String> inScopes;
	private Set<String> outScopes;
	private List<DeliverableMap> deliverables;
	private List<MilestoneMap> milestones;
	private List<PersonnelMap> personnels;
	private List<ActivityMap> activities;
	@Transient
	private boolean overrideMS;
	private List<MandaysSummary> mandaysSummaries;
	private double sellingPrice;
	private List<String> comments;
	private List<Review> reviewers;
	private TpvStatus tpvStatus = TpvStatus.DRAFT;

	public double getTotalCost() {
		if (this.mandaysSummaries == null)
			return 0;
		return this.mandaysSummaries.stream().mapToDouble(MandaysSummary::getTotalCost).sum();

	}

	public double getTotalBills() {
		if (this.mandaysSummaries == null)
			return 0;
		return this.mandaysSummaries.stream().mapToDouble(MandaysSummary::getTotalBilling).sum();
	}

	public double getTotalMandays() {
		if (this.mandaysSummaries == null)
			return 0;
		return this.mandaysSummaries.stream().mapToInt(MandaysSummary::getMandays).sum();
	}

	public double getGrossMargin() {
		return getSellingPrice() - getTotalCost();
	}

	public double getGMPercent() {
		if (getSellingPrice() == 0)
			return 0;
		return getGrossMargin() / getSellingPrice();
	}

	public double getSellingPrice() {
		return (this.sellingPrice == 0) ? getTotalBills() : this.sellingPrice;
	}

	public MandaysService(String projectId) {
		super();
		this.projectId = projectId;
	}

}
