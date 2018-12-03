package com.nuvola.tpv.model;

import java.util.Collection;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Out of Pocket Expenses
 * 
 * @author kukuhargaditya
 *
 */
public @Data @NoArgsConstructor class OutOfPocket {
	@Id
	private String id;
	private String projectId;
	private float limit;
	private Collection<CostItem> costItems;

	public OutOfPocket(String projectId) {
		super();
		this.projectId = projectId;
	}
}
