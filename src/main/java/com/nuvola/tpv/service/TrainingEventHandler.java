package com.nuvola.tpv.service;

import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.repo.TrainingRepository;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(Training.class)
public class TrainingEventHandler {
	private static Logger log = Logger.getLogger(TrainingEventHandler.class.getName());

	
	@Autowired
	private TrainingRepository trainingRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@HandleAfterCreate
	public void handleTrainingCreate(Training training) {
		log.info("==create training for project==" + training.getProjectId());
		Project project = projectRepository.findById(training.getProjectId()).get();
		setTrainingRevenueAndCost(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}

	@HandleAfterSave
	public void handleTrainingUpdate(Training training) {
		log.info("==update training for project==" +  training.getProjectId());
		Project project = projectRepository.findById(training.getProjectId()).get();
		setTrainingRevenueAndCost(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}
	
	private void setTrainingRevenueAndCost(Project project) {
		Collection<Training> trainingList = trainingRepository.findByProjectId(project.getId());
		if (trainingList == null)
			return;
		double totalRevenue = trainingList.stream().collect(Collectors.summingDouble(n -> n.getSellingPrice()));
		double totalCost = trainingList.stream().collect(Collectors.summingDouble(n -> n.getFinalCost()));
		project.setRevenue(totalRevenue);
		project.setCost(totalCost);
	}
}