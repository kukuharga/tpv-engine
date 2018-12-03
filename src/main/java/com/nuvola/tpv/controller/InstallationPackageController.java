package com.nuvola.tpv.controller;


import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.InstallationPackage;
import com.nuvola.tpv.repo.InstallationPackageRepository;


@RestController
@RequestMapping("/installationPackages")
public class InstallationPackageController {

	@Autowired
	private InstallationPackageRepository packageRepository;
	

	@GetMapping(value="/combo")
	public Map<String, String> getAllPackageMap(){
		return packageRepository.findAll().stream().collect(Collectors.toMap(InstallationPackage::getCode, InstallationPackage::getTitle));
	}

	
	


}