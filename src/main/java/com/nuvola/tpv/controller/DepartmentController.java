package com.nuvola.tpv.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nuvola.tpv.model.Department;
import com.nuvola.tpv.repo.DepartmentRepository;


@RestController
@RequestMapping("/departments")
public class DepartmentController {

	@Autowired
	private DepartmentRepository deptRepository;
	
	@CrossOrigin(origins = "*",methods = {RequestMethod.OPTIONS,RequestMethod.GET},allowedHeaders = "*")
	@GetMapping("/")
	public List<Department>getAll(){
		 System.out.println("=======get all departments======");
		return deptRepository.findAll();
	}


}