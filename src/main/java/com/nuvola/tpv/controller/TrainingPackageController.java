package com.nuvola.tpv.controller;


import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.TrainingPackage;
import com.nuvola.tpv.repo.TrainingPackageRepository;


@RestController
@RequestMapping("/trainingPackages")
public class TrainingPackageController {

	@Autowired
	private TrainingPackageRepository packageRepository;
	

	@GetMapping(value="/combo")
	public Map<String, String> getAllPackageMap(){
		return packageRepository.findAll().stream().collect(Collectors.toMap(TrainingPackage::getCode, TrainingPackage::getTitle));
	}

	
	


}