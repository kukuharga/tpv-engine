package com.nuvola.tpv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.UserRepository;


@Component
public class UserService {
//	private static Log log = LogFactory.getLog(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	public String getFullName(String userName) {
		if(CommonUtils.isEmpty(userName)) return "";
		User user = userRepository.findById(userName).orElse(null);
		return (user != null) ? user.getFullName() : "";
	}
	

}
