package com.nuvola.tpv.service;


import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.OutOfPocket;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.repo.OutOfPocketRepository;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(Installation.class)
public class OutOfPocketEventHandler {
	private static Logger log = Logger.getLogger(OutOfPocketEventHandler.class.getName());

	
	@Autowired
	private OutOfPocketRepository outOfPocketRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@HandleAfterCreate
	public void handleOutOfPocketCreate(OutOfPocket outOfPocket) {
		log.info("==create out of pocket for project==" + outOfPocket.getProjectId());
		Project project = projectRepository.findById(outOfPocket.getProjectId()).get();
		setOutOfPocketExpense(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}

	@HandleAfterSave
	public void handleOutOfPocketUpdate(OutOfPocket outOfPocket) {
		log.info("==update installation for project==" +  outOfPocket.getProjectId());
		Project project = projectRepository.findById(outOfPocket.getProjectId()).get();
		setOutOfPocketExpense(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}
	
	private void setOutOfPocketExpense(Project project) {
		OutOfPocket outOfPocket = outOfPocketRepository.findFirstByProjectId(project.getId());
		if (outOfPocket == null) return;
		double totalExpense = outOfPocket.getCostItems().stream().collect(Collectors.summingDouble(n -> n.getTotal()));
		project.setOutOfPocket(totalExpense);
	}
}