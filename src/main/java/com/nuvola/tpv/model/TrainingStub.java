package com.nuvola.tpv.model;


import java.io.Serializable;
import java.util.Collection;


import lombok.Data;

public @Data class TrainingStub implements Serializable {

	private static final long serialVersionUID = -4326299426397012421L;
	private String projectId;
	private String trainingPackage;
	private int participantCount;
	private double participantCost;
	private Collection<String>requiredItems;
	private Collection<CostItem>costItems;
}
