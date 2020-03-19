package com.nuvola.tpv.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.Task;
import com.nuvola.tpv.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	@Autowired
	private TaskService taskService;
	

	@GetMapping(value="/myTaskList")
	public List<Task> getTaskListByAssignee(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = authentication.getName();
		return taskService.findByAssignee(userName);
	}

	
	
	
	


}