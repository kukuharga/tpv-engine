package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;
import com.nuvola.tpv.model.Names.LobType;
import com.nuvola.tpv.model.Names.ReviewerType;
import com.nuvola.tpv.model.Names.UserType;

import lombok.Data;

public @Data class Reviewer {
	@Id
	private String id;
	private ReviewerType reviewerType;
	private String reviewer;
	private LobType lobType;
	private UserType userType;
}
