package com.nuvola.tpv.model;

public class Names {
	public static String DRAFT = "DRAFT";
	public static String SUBMITTED = "SUBMITTED";
	public static String APPROVED = "APPROVED";
	public static String DECLINED = "DECLINED";

	public enum DocStatus {
		DRAFT, WAITING_TPV_SUBMISSION, WAITING_TPV_APPROVAL,TPV_APPROVED, TPV_DECLINED, WAITING_RLT_APPROVAL, APPROVED, DECLINED
	}

	public enum TpvStatus {
		DRAFT, SUBMITTED
	}

	public enum ReviewStatus {
		APPROVED, DECLINED, PENDING_APPROVAL
	}

	public enum ReviewerType {
		PROJECT, LOB
	}

	public enum LobType {
		MANDAYS, TRAINING, INSTALL, PURCHASE
	}

	public enum UserType {
		USER, GROUP
	}

	public enum DecisionType {
		INDIVIDUAL, REPRESENTATIVE
	}

	public enum CommentType {
		PROJECT, LOB
	}

	public enum TaskScope {
		PROJECT, LOB
	}

	public enum TaskType {
		CREATE_TPV, REVIEW_RLT, REVIEW_TPV
	}

	public enum TaskStatus {
		OPEN, COMPLETED
	}
}
