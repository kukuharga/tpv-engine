package com.nuvola.tpv.controller;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.CostItem;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.InstallationStub;
import com.nuvola.tpv.service.InstallationService;

@RestController
@RequestMapping("/installations")
public class InstallationController {

	@Autowired
	private InstallationService installationService;
	

	@PostMapping(value="/defaultCosts")
	public Collection<CostItem> getDefaultCosts(@RequestBody InstallationStub stub){
		System.out.println("stub:"+Arrays.toString(stub.getRequiredItems().toArray()));
		return installationService.getCostItemList(stub.getInstPackage(),stub.getRequiredItems());
	}
//	public Collection<CostItem> getDefaultCosts(@RequestBody InstallationStub stub){
//		System.out.println("stub:"+Arrays.toString(stub.getCostItems().toArray()));
//		return installationService.getCostItemList(stub.getInstPackage(),stub.getRequiredItems());
//	}
	
	@PostMapping(value="/preview")
	public Installation getInstallation(@RequestBody InstallationStub stub){
		return installationService.getInstallation(stub);
	}
	
	
	


}