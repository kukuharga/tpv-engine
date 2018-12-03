package com.nuvola.tpv.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nuvola.tpv.model.DeliverableMap;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.OutOfPocket;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Purchase;
import com.nuvola.tpv.model.PurchaseOrderQueryStub;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.repo.InstallationRepository;
import com.nuvola.tpv.repo.MandaysServiceRepository;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.PurchaseRepository;
import com.nuvola.tpv.repo.TrainingRepository;
import com.nuvola.tpv.service.MandaysServiceService;
import com.nuvola.tpv.service.OutOfPocketService;
import com.nuvola.tpv.service.ProjectService;

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
	private TrainingRepository trainingRepository;
	@Autowired
	private MandaysServiceService mandaysService;
	@Autowired
	private MandaysServiceRepository serviceRepository;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private OutOfPocketService outOfPocketService;

	@GetMapping("/bySales")
	public List<Project> getProjectsBySales(String service) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged on user : " + authentication.getName());
		return projectService.getProjectsBySalesCombo(authentication.getName(),service);
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
		return trainingRepository.findByProjectId(projectId);
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