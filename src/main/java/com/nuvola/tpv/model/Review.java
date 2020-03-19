package com.nuvola.tpv.model;

import java.util.Date;

import com.nuvola.tpv.model.Names.DecisionType;
import com.nuvola.tpv.model.Names.ReviewStatus;
import com.nuvola.tpv.model.Names.ReviewerType;

import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class Review {
	private String reviewer;
	private String userGroup;
	private ReviewStatus status;
	private ReviewerType reviewerType;
	private Date responseDate;
	private DecisionType decisionType = DecisionType.INDIVIDUAL;
	
	public Review(String reviewer, ReviewerType reviewerType,DecisionType decisionType) {
		super();
		this.reviewer = reviewer;
		this.reviewerType = reviewerType;
		this.decisionType = decisionType;
	}
	
	public Review(String reviewer, ReviewerType reviewerType,DecisionType decisionType,String userGroup) {
		super();
		this.reviewer = reviewer;
		this.reviewerType = reviewerType;
		this.decisionType = decisionType;
		this.userGroup = userGroup;
	}
	
	public String getDecisionKey() {
		return (DecisionType.REPRESENTATIVE == this.decisionType) ? this.userGroup : this.reviewer;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Review && obj != null) {
			Review review = (Review)obj;
			return (review.getReviewer().equals(this.reviewer) && review.getReviewerType().equals(this.reviewerType));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	
}
