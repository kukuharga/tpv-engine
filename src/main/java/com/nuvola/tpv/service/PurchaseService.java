package com.nuvola.tpv.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Purchase;
import com.nuvola.tpv.repo.PurchaseRepository;
@Component
public class PurchaseService {
	@Autowired
	private PurchaseRepository purchaseRepository;
	
	public void setRevenueAndCost(Project project) {
		Collection<Purchase> purchaseList = purchaseRepository.findByProjectId(project.getId());
		if (purchaseList == null)
			return;
		double totalRevenue = purchaseList.stream().collect(Collectors.summingDouble(n -> n.getPriceSell()));
		double totalCost = purchaseList.stream().collect(Collectors.summingDouble(n -> n.getPriceBuy()));
		project.setRevenue(totalRevenue);
		project.setCost(totalCost);
	}
}
