package com.nuvola.tpv.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.UserRepository;


@RestController
@RequestMapping("/groups")
public class GroupController {

	
	@Autowired
	private UserRepository userRepository;

	
	@GetMapping("/{id}/users")
	public List<User>getUsersByGroup(@PathVariable(name="id")String groupCode) {
//		Set<String>groupCodes = new HashSet<String>();
//		groupCodes.add(groupCode);
		return userRepository.findByGroups(groupCode);
	}
}