package com.nuvola.tpv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.CostItem;
import com.nuvola.tpv.model.DefaultCostItem;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.InstallationPackage;
import com.nuvola.tpv.model.InstallationStub;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.model.UserGroup;
import com.nuvola.tpv.repo.DefaultCostItemRepository;
import com.nuvola.tpv.repo.InstallationPackageRepository;
import com.nuvola.tpv.repo.InstallationRepository;
import com.nuvola.tpv.repo.UserGroupRepository;
import com.nuvola.tpv.repo.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class InstallationService {
	@Autowired
	private DefaultCostItemRepository itemRepository;
//	@Autowired
//	private InstallationRepository installationRepository;
	@Autowired
	private InstallationPackageRepository installationPkgRepository;
	public static final String ACCOMMODATION = "ACCOM";
	public static final String BT = "BT";
	public static final String WARRANTY_SVC = "WARRANTY_SVC";
	public static final String TRANSPORT = "TRANSPORT";
	private static final String SEPARATOR = " - ";
	public static final String INSTALLATION = "INSTL";
	public static final String TRAINING = "TRG";

	public Collection<CostItem> getCostItemList(String pkgCode,Collection<String> itemList) {
		InstallationPackage pkg = installationPkgRepository.findById(pkgCode).get();
		List<DefaultCostItem> defaultItemList = itemRepository.findByServiceAndCategoryIn(INSTALLATION,itemList);
		return applyRules(defaultItemList,pkg).stream().map(n -> n.getCostItem()).collect(Collectors.toList());
	}

	private List<DefaultCostItem> applyRules(List<DefaultCostItem> collect, InstallationPackage pkg) {
		int personnelCount = pkg.getEngineerCount() + pkg.getAssistantCount();
		int noOfNights =  Double.valueOf(Math.ceil((personnelCount) / 2f) * (pkg.getWorkingDays() - 1)).intValue();

		for (DefaultCostItem item:collect) {
			if(ACCOMMODATION.equalsIgnoreCase(item.getCategory()) && pkg.getWorkingDays() > 1) {
				item.getCostItem().setQuantity(noOfNights);
			}else if(TRANSPORT.equalsIgnoreCase(item.getCategory())) {
				item.getCostItem().setQuantity(personnelCount);
			}else if(BT.equalsIgnoreCase(item.getCategory())) {
				item.getCostItem().setQuantity(pkg.getWorkingDays());
			}
			
		}
		return collect;
	}

	public Installation getInstallation(InstallationStub stub) {
		InstallationPackage pkg = installationPkgRepository.findById(stub.getInstPackage()).get();
		Installation inst = new Installation();
		inst.setCostItems(stub.getCostItems());
		inst.setExtraSvrCost(stub.getSvrCharge());
		inst.setSvrCount(stub.getSvrCount());
		inst.setProjectId(stub.getProjectId());
		inst.setAssistantCount(pkg.getAssistantCount());
		inst.setEngineerCount(pkg.getEngineerCount());
		inst.setBrAssistant(pkg.getBrAssistant());
		inst.setLrAssistant(pkg.getLrAssistant());
		inst.setBrEngineer(pkg.getBrEngineer());
		inst.setLrEngineer(pkg.getLrEngineer());
		inst.setTitle(pkg.getDescription() + SEPARATOR + pkg.getType() + SEPARATOR + pkg.getLocation());
		inst.setWorkingDays(pkg.getWorkingDays());
		inst.setMinServer(pkg.getMinServer());
		inst.setInstPackage(pkg.getCode());
		inst.setType(pkg.getType());
		inst.setLocation(pkg.getLocation());
		inst.setSellingPrice(inst.getFinalCost() * 1.7);
		inst.setRequiredItems(stub.getRequiredItems());
		return inst;
	}
	
	

}
