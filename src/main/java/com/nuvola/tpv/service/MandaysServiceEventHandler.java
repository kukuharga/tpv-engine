package com.nuvola.tpv.service;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.repo.MandaysServiceRepository;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(MandaysService.class)
public class MandaysServiceEventHandler {
	private static Logger log = Logger.getLogger(MandaysServiceEventHandler.class.getName());

	@Autowired
	private MandaysServiceService service;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private MandaysServiceRepository mandaysRepository;

	

	@HandleBeforeSave
	public void handleMandaysServiceUpdate(MandaysService mandaysService) {
		log.info("==update mandays==" + mandaysService.getProjectId());

		// Calculate & Set Personnel Bills and Cost
//		if (mandaysService.getActivities() != null && !mandaysService.getActivities().isEmpty()) {

			// Assign mandays by role value.
			service.assignMandaysSummary(mandaysService);
//		}

	}

	@HandleAfterSave
	public void handleAfterMandaysSave(MandaysService mandaysService) {
		log.info("==handle mandays after save for project==" + mandaysService.getProjectId());
		Project project = projectRepository.findById(mandaysService.getProjectId()).get();
//		setMandaysRevenueAndCost(project);
		mandaysService.setMandaysSummaries(service.getMandaysSummaryList(mandaysService));
		project.setRevenue(mandaysService.getTotalBills());
		project.setCost(mandaysService.getTotalCost());
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}

	/**
	 * Calculate and set project revenue and cost
	 * 
	 * @param project
	 */
//	private void setMandaysRevenueAndCost(Project project) {
//		MandaysService mandaysService = mandaysRepository.findFirstByProjectId(project.getId());
//		if (mandaysService == null)
//			return;
//		project.setRevenue(mandaysService.getTotalBills());
//		project.setCost(mandaysService.getTotalCost());
//
//	}

}