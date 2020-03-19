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
import com.nuvola.tpv.model.Names.DocStatus;
import com.nuvola.tpv.model.Names.TpvStatus;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(MandaysService.class)
public class MandaysServiceEventHandler {
	private static Logger log = Logger.getLogger(MandaysServiceEventHandler.class.getName());

	@Autowired
	private MandaysServiceService service;
	@Autowired
	private ProjectRepository projectRepo;

	
	@HandleBeforeCreate
	public void handleMandaysServiceBeforeCreate(MandaysService mandaysService) {
		log.info("==update mandays==" + mandaysService.getProjectId());

		// Assign mandays by role value.
		service.assignMandaysSummary(mandaysService);
		
		

	}

	@HandleBeforeSave
	public void handleMandaysServiceBeforeSave(MandaysService mandaysService) {
		// Assign mandays by role value.
		service.assignMandaysSummary(mandaysService);
	}

	@HandleAfterSave
	public void handleAfterMandaysSave(MandaysService mandaysService) {
		log.info("==handle mandays after save for project==" + mandaysService.getProjectId());
		Project project = projectRepo.findById(mandaysService.getProjectId()).get();
		DocStatus[]blackList = {DocStatus.DRAFT, DocStatus.WAITING_TPV_SUBMISSION, DocStatus.WAITING_TPV_APPROVAL};
		boolean notInBlackList = Arrays.stream(blackList).anyMatch(b->project.getDocStatus() != b);
		if(notInBlackList) { 
			service.setRevenueAndCost(project,mandaysService);
			projectRepo.save(project);
		}
		
		log.info("==project cost info updated successfully==");
	}



}