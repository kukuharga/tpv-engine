package com.nuvola.tpv.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.UserRepository;

@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {
	private static Logger log = Logger.getLogger(UserEventHandler.class.getName());

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@HandleBeforeCreate
	public void handleUserCreate(User user) {
		log.info("==register username==" + user.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	}

	@HandleBeforeSave
	public void handleUserUpdate(User user) {
		log.info("==update username==" + user.getUsername());
		User storedUser = userRepository.findById(user.getUsername()).get();
		boolean passwordChanged = !StringUtils.isEmpty(user.getPassword()) && !user.getPassword().equals(storedUser.getPassword());
		
		if(passwordChanged) {
			// password change request
			log.info("==change password==" + user.getUsername());
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}else {
			log.info("==no change password==" + user.getUsername());
		}
	}
}