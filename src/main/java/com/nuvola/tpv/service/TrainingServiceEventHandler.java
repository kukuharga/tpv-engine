package com.nuvola.tpv.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.Names.DocStatus;
import com.nuvola.tpv.model.Names.TpvStatus;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(TrainingService.class)
public class TrainingServiceEventHandler {
	private static Logger log = Logger.getLogger(TrainingServiceEventHandler.class.getName());

	@Autowired
	private TrainingService service;
	@Autowired
	private ProjectRepository projectRepo;

	



	@HandleAfterSave
	public void handleAfterTrainingSave(Training training){
		log.info("==handle training after save for project==" + training.getProjectId());
		Project project = projectRepo.findById(training.getProjectId()).get();
		DocStatus[]blackList = {DocStatus.DRAFT, DocStatus.WAITING_TPV_SUBMISSION, DocStatus.WAITING_TPV_APPROVAL};
		boolean notInBlackList = Arrays.stream(blackList).anyMatch(b->project.getDocStatus() != b);
		if(notInBlackList) {
			service.setRevenueAndCost(project);
			projectRepo.save(project);
		}
		log.info("==project cost info updated successfully==");
	}



}