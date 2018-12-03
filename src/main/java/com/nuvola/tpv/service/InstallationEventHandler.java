package com.nuvola.tpv.service;

import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.repo.InstallationRepository;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(Installation.class)
public class InstallationEventHandler {
	private static Logger log = Logger.getLogger(InstallationEventHandler.class.getName());

	
	@Autowired
	private InstallationRepository installationRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@HandleAfterCreate
	public void handleInstallationCreate(Installation installation) {
		log.info("==create installation for project==" + installation.getProjectId());
		Project project = projectRepository.findById(installation.getProjectId()).get();
		setInstallationRevenueAndCost(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}

	@HandleAfterSave
	public void handleInstallationUpdate(Installation installation) {
		log.info("==update installation for project==" +  installation.getProjectId());
		Project project = projectRepository.findById(installation.getProjectId()).get();
		setInstallationRevenueAndCost(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}
	
	private void setInstallationRevenueAndCost(Project project) {
		Collection<Installation> installationList = installationRepository.findByProjectId(project.getId());
		if (installationList == null)
			return;
		double totalRevenue = installationList.stream().collect(Collectors.summingDouble(n -> n.getSellingPrice()));
		double totalCost = installationList.stream().collect(Collectors.summingDouble(n -> n.getFinalCost()));
		project.setRevenue(totalRevenue);
		project.setCost(totalCost);
	}
}