package com.nuvola.tpv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.CostItem;
import com.nuvola.tpv.model.DefaultCostItem;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.TrainingPackage;
import com.nuvola.tpv.model.TrainingSet;
import com.nuvola.tpv.model.TrainingStub;
import com.nuvola.tpv.repo.DefaultCostItemRepository;
import com.nuvola.tpv.repo.TrainingPackageRepository;
import com.nuvola.tpv.repo.TrainingRepository;
import com.nuvola.tpv.repo.TrainingSetRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TrainingService {
	@Autowired
	private DefaultCostItemRepository itemRepository;
	@Autowired
	private TrainingPackageRepository trainingPkgRepository;
	@Autowired
	private TrainingRepository trainingRepository;
	@Autowired
	private TrainingSetRepository trainingSetRepository;
	public static final String ACCOMMODATION = "ACCOM";
	public static final String BT = "BT";
	public static final String WARRANTY_SVC = "WARRANTY_SVC";
	public static final String TRANSPORT = "TRANSPORT";
	public static final String SPECIAL_VENUE = "VENUE";
	private static final String SEPARATOR = " - ";
	public static final String TRAINING = "TRG";

	public Collection<CostItem> getCostItemList(String pkgCode,Collection<String> itemList) {
		TrainingPackage pkg = trainingPkgRepository.findById(pkgCode).get();
		List<DefaultCostItem> defaultItemList = itemRepository.findByServiceAndCategoryIn(TRAINING,itemList);
		return applyRules(defaultItemList,pkg).stream().map(n -> n.getCostItem()).collect(Collectors.toList());
	}

	private List<DefaultCostItem> applyRules(List<DefaultCostItem> collect, TrainingPackage pkg) {
		int personnelCount = pkg.getInstructorCount() + pkg.getAssistantCount();
		int noOfNights =  Double.valueOf(Math.ceil((personnelCount) / 2f) * (pkg.getWorkingDays() - 1)).intValue();

		for (DefaultCostItem item:collect) {
			if(ACCOMMODATION.equalsIgnoreCase(item.getCategory()) && pkg.getWorkingDays() > 1) {
				item.getCostItem().setQuantity(noOfNights);
			}else if(TRANSPORT.equalsIgnoreCase(item.getCategory())) {
				item.getCostItem().setQuantity(personnelCount);
			}else if(BT.equalsIgnoreCase(item.getCategory())) {
				item.getCostItem().setQuantity(pkg.getWorkingDays());
			}
			
		}
		return collect;
	}

	public Training getTraining(TrainingStub stub) {
		TrainingPackage pkg = trainingPkgRepository.findById(stub.getTrainingPackage()).get();
		Training inst = new Training();
		inst.setCostItems(stub.getCostItems());
		inst.setParticipantCost(stub.getParticipantCost());
		inst.setParticipantCount(stub.getParticipantCount());
		inst.setProjectId(stub.getProjectId());
		inst.setAssistantCount(pkg.getAssistantCount());
		inst.setInstructorCount(pkg.getInstructorCount());
		inst.setBrAssistant(pkg.getBrAssistant());
		inst.setLrAssistant(pkg.getLrAssistant());
		inst.setBrInstructor(pkg.getBrInstructor());
		inst.setLrInstructor(pkg.getLrInstructor());
		inst.setTitle(pkg.getDescription() + SEPARATOR + pkg.getType() + SEPARATOR + pkg.getLocation());
		inst.setWorkingDays(pkg.getWorkingDays());
		inst.setMinParticipant(pkg.getMinParticipant());
		inst.setTrainingPackage(pkg.getCode());
		inst.setServerCost(pkg.getServerCost());
		inst.setCourseMaterialFee(pkg.getCourseMaterialFee());
		inst.setRefreshmentCost(pkg.getRefreshmentCost());
		inst.setType(pkg.getType());
		inst.setLocation(pkg.getLocation());
		inst.setType("TRAINING");
//		inst.setSellingPrice(inst.getFinalCost() * 1.7);
		inst.setRequiredItems(stub.getRequiredItems());
		return inst;
	}
	
	
	
	public Collection<Training> getTrainings(String projectId) {
		return trainingRepository.findByProjectId(projectId);
	}
	
	public TrainingSet getTrainingSet(String projectId) {

		
		TrainingSet trainingSet = trainingSetRepository.findFirstByProjectId(projectId);
		if(trainingSet == null) {
			trainingSet = new TrainingSet();
			trainingSet.setProjectId(projectId);
			Collection<Training> trainings = getTrainings(projectId);
			Set<String> trainingIds = trainings.stream().map(n->n.getCode()).collect(Collectors.toSet());
			trainingSet.setTrainingIds(trainingIds);
			trainingSet = trainingSetRepository.save(trainingSet);
		}	
		
		return trainingSet;
	}
	
	public TrainingSet saveTrainingSet(TrainingSet trainingSet) {
		return trainingSetRepository.save(trainingSet);
	}
	
	public void setRevenueAndCost(Project project) {
		Collection<Training> trainingList = trainingRepository.findByProjectId(project.getId());
		if (trainingList == null) return;
		setRevenueAndCost(project, trainingList);
	}
	
	public void setRevenueAndCost(Project project,Collection<Training> trainingList) {
		double totalRevenue = trainingList.stream().collect(Collectors.summingDouble(n -> n.getSellingPrice()));
		double totalCost = trainingList.stream().collect(Collectors.summingDouble(n -> n.getFinalCost()));
		project.setRevenue(totalRevenue);
		project.setCost(totalCost);
	}
	
	

}
