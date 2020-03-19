package com.nuvola.tpv.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.nuvola.tpv.model.Names.TpvStatus;

import lombok.Data;


public @Data  class TrainingSet extends Auditable<String> implements Serializable{
	
	private static final long serialVersionUID = 1224417855307589953L;
	@Id
	private String code;
	private String projectId;
	Set<String>trainingIds = new HashSet<>();
	private TpvStatus tpvStatus = TpvStatus.DRAFT;
	@Transient
	Collection<Training>trainingList;
	
	public void addTrainingId(String trainingId) {
		trainingIds.add(trainingId);
	}
	
}
