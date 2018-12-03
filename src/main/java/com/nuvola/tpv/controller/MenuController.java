package com.nuvola.tpv.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nuvola.tpv.model.DeliverableMap;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.OutOfPocket;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.Purchase;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.repo.InstallationRepository;
import com.nuvola.tpv.repo.MandaysServiceRepository;
import com.nuvola.tpv.repo.MenuRepository;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.PurchaseRepository;
import com.nuvola.tpv.repo.TrainingRepository;
import com.nuvola.tpv.service.MandaysServiceService;
import com.nuvola.tpv.service.OutOfPocketService;
import com.nuvola.tpv.service.ProjectService;

@RestController
@RequestMapping("/menus")
public class MenuController {

	@Autowired
	private MenuRepository menuRepository;
	
	

}