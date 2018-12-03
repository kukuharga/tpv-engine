package com.nuvola.tpv.service;

import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.Purchase;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.repo.PurchaseRepository;
import com.nuvola.tpv.repo.ProjectRepository;

@Component
@RepositoryEventHandler(Purchase.class)
public class PurchaseEventHandler {
	private static Logger log = Logger.getLogger(PurchaseEventHandler.class.getName());

	
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@HandleAfterCreate
	public void handlePurchaseCreate(Purchase purchase) {
		log.info("==create purchase for project==" + purchase.getProjectId());
		Project project = projectRepository.findById(purchase.getProjectId()).get();
		setPurchaseRevenueAndCost(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}

	@HandleAfterSave
	public void handlePurchaseUpdate(Purchase purchase) {
		log.info("==update purchase for project==" +  purchase.getProjectId());
		Project project = projectRepository.findById(purchase.getProjectId()).get();
		setPurchaseRevenueAndCost(project);
		project = projectRepository.save(project);
		log.info("==project cost info updated successfully==");
	}
	
	private void setPurchaseRevenueAndCost(Project project) {
		Collection<Purchase> purchaseList = purchaseRepository.findByProjectId(project.getId());
		if (purchaseList == null)
			return;
		double totalRevenue = purchaseList.stream().collect(Collectors.summingDouble(n -> n.getPriceSell()));
		double totalCost = purchaseList.stream().collect(Collectors.summingDouble(n -> n.getPriceBuy()));
		project.setRevenue(totalRevenue);
		project.setCost(totalCost);
	}
	
	
}