package com.nuvola.tpv.model;

import java.util.Map;
import java.util.Set;

import lombok.Data;

public @Data class LineOfBusiness {
	private Map<String,String>approvers;
	private String status;
	
}
