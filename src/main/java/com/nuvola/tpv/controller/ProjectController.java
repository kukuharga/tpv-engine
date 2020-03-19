package com.nuvola.tpv.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.Comment;
import com.nuvola.tpv.model.DeliverableMap;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.OutOfPocket;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Purchase;
import com.nuvola.tpv.model.Review;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.Names.DecisionType;
import com.nuvola.tpv.model.Names.ReviewStatus;
import com.nuvola.tpv.model.Names.ReviewerType;
import com.nuvola.tpv.repo.InstallationRepository;
import com.nuvola.tpv.repo.MandaysServiceRepository;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.PurchaseRepository;
import com.nuvola.tpv.service.CommentService;
import com.nuvola.tpv.service.MandaysServiceService;
import com.nuvola.tpv.service.OutOfPocketService;
import com.nuvola.tpv.service.ProjectService;
import com.nuvola.tpv.service.TrainingService;
	
@RestController
@RequestMapping("/projects")
public class ProjectController {
	
	private static Log log = LogFactory.getLog(ProjectController.class);
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private InstallationRepository installationRepository;
	@Autowired
	private TrainingService trainingService;
	@Autowired
	private MandaysServiceService mandaysService;
	@Autowired
	private MandaysServiceRepository serviceRepository;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private OutOfPocketService outOfPocketService;
	@Autowired
	private CommentService commentService;

	@GetMapping("/fin/{projectId}")
	public Project getProjectById(@PathVariable(name = "projectId") String projectId) {
		return projectService.getProjectById(projectId);
	}
	
	@GetMapping("/bySales")
	public List<Project> getProjectsBySales(String service) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged on user : " + authentication.getName());
		return projectService.getProjectsBySales(authentication.getName(),service);
	}
	
	@GetMapping("/subProjects")
	public List<Project> getSubProjectsBySales(String service) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged on user : " + authentication.getName());
		return projectService.getSubProjectsBySales(authentication.getName(),service);
	}

	@GetMapping("/bySalesCombo")
	public Map<String, String> getProjectsBySalesCombo(String service) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged on user : " + authentication.getName());
		return projectService.getProjectsBySalesGroupCombo(authentication.getName(),service);
	}
	
	@GetMapping("/byStageCombo/{stageId}")
	public Map<String, String> getProjectsCombo(@PathVariable(name="stageId")String stageId) {
		List<Project> projects = projectRepository.findByLeadStage(stageId);
		if(projects == null || projects.isEmpty()) return null;
		Map<String, String> projectMap = new TreeMap<String, String>();
		projects.forEach((k) -> projectMap.put(k.getId(),k.getCode() +" - "+ k.getName()));
		return projectMap;
	}

	@GetMapping("/{projectId}/purchases")
	public Collection<Purchase> getPurchases(@PathVariable(name = "projectId") String projectId) {
		return purchaseRepository.findByProjectId(projectId);
	}

	@GetMapping("/{projectId}/installations")
	public Collection<Installation> getInstallations(@PathVariable(name = "projectId") String projectId) {
		return installationRepository.findByProjectId(projectId);
	}

	@GetMapping("/{projectId}/trainings")
	public Collection<Training> getTrainings(@PathVariable(name = "projectId") String projectId) {
		return trainingService.getTrainings(projectId);
	}
	
	@PostMapping("/multiple")
	public List<Project> getProjectByProjectIds(@RequestBody Set<String> projectIds) {
		List<Project> target = new ArrayList<>();
		projectRepository.findAllById(projectIds).forEach(target::add);
		return target;
	}

//	@GetMapping("/{projectId}")
//	public Project getProject(@PathVariable(name = "projectId") String projectId) {
//
//		return projectService.getProject(projectId);
//	}

	@GetMapping("/{projectId}/deptPurchases")
	public Collection<Purchase> getPurchasesByDept(@PathVariable(name = "projectId") String projectId) {
		Collection<Purchase> purchases = purchaseRepository.findByProjectId(projectId);

		Map<String, List<Purchase>> totalByDept = purchases.stream()
				.collect(Collectors.groupingBy(Purchase::getDepartment));
		List<Purchase> deptPurchase = new ArrayList<Purchase>();
		totalByDept.forEach((k, v) -> {
			Purchase p = new Purchase();
			p.setDepartment(k);
			v.forEach(pur -> {
				p.setPriceBuy(p.getPriceBuy() + pur.getPriceBuy());
				p.setPriceSell(p.getPriceSell() + pur.getPriceSell());
			});
			deptPurchase.add(p);
		});
		return deptPurchase;

	}

	@PostMapping("/{projectId}/purchases")
	public Collection<Purchase> savePurchases(@PathVariable(name = "projectId") String projectId,
			@RequestBody List<Purchase> purchaseList) {
		Project project = projectRepository.findById(projectId).get();
		purchaseList.forEach((purchase) -> purchase.setProjectId(project.getId()));
		purchaseRepository.deleteByProjectId(project.getId());
		return purchaseRepository.saveAll(purchaseList);
	}
	
	

	@GetMapping("/{projectId}/deliverables")
	public Collection<DeliverableMap> getDeliverables(@PathVariable(name = "projectId") String projectId) {
		MandaysService md = serviceRepository.findDeliverablesByProjectId(projectId);
		if (md == null)
			throw new NoSuchElementException("Project ID not found");
		return md.getDeliverables();
	}

	@GetMapping("/{projectId}/mandaysService")
	public MandaysService getMandaysService(@PathVariable(name = "projectId") String projectId) {

		return mandaysService.getMandaysService(projectId);
	}
	
	@GetMapping("/{projectId}/outOfPocket")
	public OutOfPocket getOutOfPocket(@PathVariable(name = "projectId") String projectId) {

		return outOfPocketService.getOutOfPocket(projectId);
	}
	
	@GetMapping("/{projectId}/comments")
	public List<Comment> getComments(@PathVariable(name = "projectId") String projectId) {

		return commentService.getProjectComment(projectId);
	}
	
	@GetMapping("/{projectId}/lobComments")
	public List<Comment> getLobComments(@PathVariable(name = "projectId") String projectId) {
		return commentService.getLobComment(projectId);
	}
	
	@GetMapping("/reviewers/{reviewerType}")
	private List<Project> getProjectByReviewer(@PathVariable(name = "reviewerType") String reviewerType){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged on user : " + authentication.getName());
		String userName = authentication.getName();
		return projectService.getProjectsByReviewer(userName,reviewerType);
	}
	
	@GetMapping("reviewers/{reviewerType}/combo/{lobType}")
	private Map<String, String> getProjectByReviewerCombo(@PathVariable(name = "reviewerType") String reviewerType, @PathVariable(name = "lobType") String lobType){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged on user : " + authentication.getName());
		String userName = authentication.getName();
		return projectService.getProjectsByReviewerCombo(userName,reviewerType,lobType);
	}
	
	@PostMapping("{projectId}/reviewers/{operationType}")
	private Project modifyReviewers(@PathVariable(name = "projectId") String projectId,@PathVariable(name = "operationType") String operationType, @RequestBody Review review){
		Project project = null;
		review.setDecisionType(DecisionType.INDIVIDUAL);
		switch (operationType) {
		case "ADD":		
			project =  projectService.addReviewer(projectId,review);
			break;
		case "REMOVE":
			project =  projectService.removeReviewer(projectId,review);
			break;
		}
		return project;
	}
	
	@PostMapping("{projectId}/associatedProjects/{operationType}/{associatedProjectId}")
	private Project modifyAssociatedProjects(@PathVariable(name = "projectId") String projectId,@PathVariable(name = "operationType") String operationType, @PathVariable(name="associatedProjectId") String associatedProjectId){
		Project project = null;
		
		switch (operationType) {
		case "ADD":		
			project =  projectService.addAssociatedProjectId(projectId,associatedProjectId);
			break;
		case "REMOVE":
			project =  projectService.removeAssociatedProjectId(projectId,associatedProjectId);
			break;
		}
		return project;
	}
	
	@PostMapping("/{projectId}/review")
	private Project setApprovalStatus(@PathVariable(name = "projectId") String projectId, @RequestBody Map<String, String> params) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = authentication.getName();
		log.info("Logged on user : " + authentication.getName());
		
		return projectService.setApprovalStatus(projectId,userName,params);
	}
	
	@GetMapping(value="/clients")
	public Collection<String> findAllClients() {
		return projectService.getAllClients();
	}
	
	@GetMapping(value = "/download-full")
	public ResponseEntity<Resource> downloadFullGet() throws Exception {
//		log.debug("Entering downloadFull..Search stub==" + stub);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"project_full.xlsx\"");
		String projectId = "5b8e5e795beac13f577bf796";
		ByteArrayResource resource = projectService.downloadProject(projectId);

		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
	}
	

	
	

}