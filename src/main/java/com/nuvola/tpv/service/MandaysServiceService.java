package com.nuvola.tpv.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.ActivityMap;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.MandaysSummary;
import com.nuvola.tpv.model.PersonnelMap;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.ResourceMandays;
import com.nuvola.tpv.model.ResourceRole;
import com.nuvola.tpv.repo.MandaysServiceRepository;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.ResourceRoleRepository;

@Component
public class MandaysServiceService {
	@Autowired
	private MandaysServiceRepository serviceRepository;
	@Autowired
	private ResourceRoleRepository roleRepository;
	@Autowired
	private ProjectRepository projectRepository;

	public MandaysService getMandaysService(String projectId) {
		// Get an existing instance from DB
		MandaysService service = serviceRepository.findFirstByProjectId(projectId);
		// Create new instance if not exist
		return service == null ? serviceRepository.save(new MandaysService(projectId)) : service;

	}

	public Map<ResourceMandays, Integer> getRoleMandays(List<ActivityMap> activities) {

		if (activities == null)
			return null;

		return activities.stream().map(n -> n.getResourceAllocations()).flatMap(List::stream).collect(Collectors
				.groupingBy(Function.identity(), Collectors.reducing(0, ResourceMandays::getMandays, Integer::sum)));

	}

	public List<PersonnelMap> getPersonnelMap(String projectId) {
		MandaysService mandaysService = serviceRepository.findPersonnelsByProjectId(projectId);
		return (mandaysService != null) ? mandaysService.getPersonnels() : null;
	}

	public void assignMandaysSummary(MandaysService mandaysService) {
		
		// True if Mandays Summary is being overrided by user
		if (mandaysService.isOverrideMS())
			// Recalculate Resource Pricing only
			assignResourceRates(mandaysService);
		else
			// Recalculate Mandays Summary from activities
			mandaysService.setMandaysSummaries(getMandaysSummaryList(mandaysService));
	}

	public void assignResourceRates(MandaysService mandaysService) {
		Project project = projectRepository.findById(mandaysService.getProjectId()).get();
		Optional<Integer>weekDuration = Optional.ofNullable(project.getWeekDuration());
		List<MandaysSummary> mandaysSummaries = mandaysService.getMandaysSummaries();
		// Lookup DB for mandays rate
		if (mandaysSummaries == null)
			return;
		
		mandaysSummaries.stream().forEach(n -> {
			ResourceRole role = roleRepository.findFirstByCodeAndLevel(n.getRoleCd(), n.getRscLevel());
			if (role != null) {
				n.setBillingRt(role.getBillingRate());
				n.setLoadedCst(role.getLoadedCost());
				n.setRoleNm(role.getName());
			}
			int divident = weekDuration.orElse(0) * n.getCount() ;
			n.setRscUtil((divident == 0) ? 0 : (float) n.getMandays() / divident);
		});
	}

	public List<MandaysSummary> getMandaysSummaryList(MandaysService mandaysService) {

		if (mandaysService.getPersonnels() == null)
			return null;

		Map<ResourceMandays, Integer> roleMandays = getRoleMandays(mandaysService.getActivities());
		if(roleMandays == null) return null;
		
		Project project = projectRepository.findById(mandaysService.getProjectId()).get();
		Optional<Integer>weekDuration = Optional.ofNullable(project.getWeekDuration());
		
		return mandaysService.getPersonnels().stream().map(n -> {
			MandaysSummary mandaysSummary = new MandaysSummary();
			mandaysSummary.setRoleCd(n.getRoleCd());
			mandaysSummary.setRoleNm(n.getRoleNm());
			mandaysSummary.setRscLevel(n.getRscLevel());
			Integer mandays = roleMandays.get(new ResourceMandays(n.getRoleCd(), n.getRscLevel()));
			mandaysSummary.setMandays(mandays == null ? 0 : mandays);
			mandaysSummary.setCount(n.getCount());
			ResourceRole role = roleRepository.findFirstByCodeAndLevel(n.getRoleCd(), n.getRscLevel());
			if (role != null) {
				mandaysSummary.setBillingRt(role.getBillingRate());
				mandaysSummary.setLoadedCst(role.getLoadedCost());
				mandaysSummary.setRoleNm(role.getName());
			}
			int divident = weekDuration.orElse(0) * n.getCount() ;
			mandaysSummary.setRscUtil((divident == 0) ? 0 :(float) mandaysSummary.getMandays() / divident);
			return mandaysSummary;
		}).collect(Collectors.toList());

	}

	public void setRevenueAndCost(Project project) {
		MandaysService mandaysService = serviceRepository.findFirstByProjectId(project.getId());
		if(mandaysService == null) return;
		setRevenueAndCost(project,mandaysService);
	}
	
	public void setRevenueAndCost(Project project,MandaysService mandaysService) {
		if(mandaysService == null) return;
		project.setRevenue(mandaysService.getTotalBills());
		project.setCost(mandaysService.getTotalCost());	
	}

}
