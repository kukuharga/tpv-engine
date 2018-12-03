package com.nuvola.tpv.controller;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.ResourceRole;
import com.nuvola.tpv.repo.ResourceRoleRepository;


@RestController
@RequestMapping("/resourceRoles")
public class ResourceRoleController {
	private static Log log = LogFactory.getLog(ResourceRoleController.class);
	@Autowired
	private ResourceRoleRepository roleRepository;
	

	@GetMapping(value="/combo")
	public Map<String, String> getAllPackageMap(){
		List<ResourceRole> resourceRoles = roleRepository.findAll();
		log.debug("resourceRoles=="+Arrays.toString(resourceRoles.toArray()));
		Map<String, String> combo = resourceRoles.stream().collect(Collectors.toMap(ResourceRole::getCode, ResourceRole::getName));
		
		return combo;
	}
	
//	@GetMapping
//	public List<ResourceRole> getAll(){
//		log.debug("Invoking find All..");
//		return roleRepository.findAll();
//	}
	
//	@GetMapping(value="/comboLevel")
//	public Map<String, String> getAllPackageMap(){
//		return roleRepository.findAll().stream().collect(Collectors.toMap(ResourceRole::getCode, ResourceRole::getName));
//	}
	
	
	


}