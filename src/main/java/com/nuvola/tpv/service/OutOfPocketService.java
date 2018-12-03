package com.nuvola.tpv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.OutOfPocket;
import com.nuvola.tpv.repo.OutOfPocketRepository;

@Component
public class OutOfPocketService {
	@Autowired
	private OutOfPocketRepository serviceRepository;



	public OutOfPocket getOutOfPocket(String projectId) {
		//Get an existing instance from DB
		OutOfPocket ope = serviceRepository.findFirstByProjectId(projectId);
		//Create new instance if not exist
		return ope == null ? serviceRepository.save(new OutOfPocket(projectId)) : ope ;
	}

}
