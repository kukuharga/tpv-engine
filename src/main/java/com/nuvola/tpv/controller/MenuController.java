package com.nuvola.tpv.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.repo.MenuRepository;

@RestController
@RequestMapping("/menus")
public class MenuController {

	@Autowired
	private MenuRepository menuRepository;
	
	

}