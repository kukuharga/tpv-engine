package com.nuvola.tpv.model;


import java.io.Serializable;
import java.util.Collection;


import lombok.Data;

public @Data class InstallationStub implements Serializable {

	private static final long serialVersionUID = -4326299426397012421L;
	private String projectId;
	private String instPackage;
	private int svrCount;
	private double svrCharge;
	private Collection<String>requiredItems;
	private Collection<CostItem>costItems;
}
