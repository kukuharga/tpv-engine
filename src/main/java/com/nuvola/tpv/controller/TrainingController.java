package com.nuvola.tpv.controller;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.CostItem;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.TrainingStub;
import com.nuvola.tpv.service.TrainingService;

@RestController
@RequestMapping("/trainings")
public class TrainingController {

	@Autowired
	private TrainingService trainingService;
	

	@PostMapping(value="/defaultCosts")
	public Collection<CostItem> getDefaultCosts(@RequestBody TrainingStub stub){
		System.out.println("stub:"+Arrays.toString(stub.getRequiredItems().toArray()));
		return trainingService.getCostItemList(stub.getTrainingPackage(),stub.getRequiredItems());
	}

	
	@PostMapping(value="/preview")
	public Training getInstallation(@RequestBody TrainingStub stub){
		return trainingService.getTraining(stub);
	}
	
	
	


}