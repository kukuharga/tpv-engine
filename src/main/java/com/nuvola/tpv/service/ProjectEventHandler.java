package com.nuvola.tpv.service;


import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Names.DocStatus;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(Project.class)
public class ProjectEventHandler {
	private static Logger log = Logger.getLogger(ProjectEventHandler.class.getName());

	 @Autowired
	 private ProjectService projectService;
	 @Autowired
	 private TaskService taskService;
	 @Autowired
	 private ProjectRepository projectRepository;


	@HandleBeforeCreate
	public void handleProjectBeforeCreate(Project project) {
		log.info("On Project Before Create..");
		
		
	}
	
	@HandleAfterCreate
	public void handleProjectAfterCreate(Project project) {
		log.info("On Project After Create..");
		
		if(DocStatus.WAITING_TPV_SUBMISSION == project.getDocStatus()) {
			taskService.onRLTDraftSubmitted(project);
		}
		

	}
	
	

	@HandleBeforeSave
	public void handleProjectBeforeSave(Project project) {
		log.info("On Project Before Save..");
		Project projectBefore = projectRepository.findById(project.getId()).get();
		if(!projectBefore.getAssociatedProjectIds().isEmpty()) {
			Iterable<Project>projects = projectRepository.findAllById(project.getAssociatedProjectIds());
			projects.forEach(p ->p.setSubProject(false));
			projectRepository.saveAll(projects);
		}
	}
	
	@HandleAfterSave
	public void handleProjectAfterSave(Project project) {
		log.info("On Project After Save..");
		
		if ( DocStatus.WAITING_TPV_SUBMISSION == project.getDocStatus()) {
			taskService.onRLTDraftSubmitted(project);
		}
		
		if (DocStatus.WAITING_TPV_APPROVAL == project.getDocStatus()) {
			taskService.onTPVSubmitted(project);
		}
		
		if(!project.getAssociatedProjectIds().isEmpty()) {
			Iterable<Project>projects = projectRepository.findAllById(project.getAssociatedProjectIds());
			projects.forEach(p -> p.setSubProject(true));
			projectRepository.saveAll(projects);
		}
	}
	
	

	
	

	

	

}