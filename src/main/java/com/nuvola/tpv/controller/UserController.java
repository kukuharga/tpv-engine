package com.nuvola.tpv.controller;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nuvola.tpv.model.Menu;
import com.nuvola.tpv.model.Role;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.MenuRepository;
import com.nuvola.tpv.repo.RoleRepository;
import com.nuvola.tpv.repo.UserRepository;
import com.nuvola.tpv.service.MenuService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private static Log log = LogFactory.getLog(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private MenuService menuService;

	@Autowired
	private RoleRepository roleRepository;

	

	public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/sign-up")
	@PreAuthorize("hasAuthority('ADMIN')")
	public User signUp(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@GetMapping(value = "/menus")
	public Collection<Menu> getMenuByUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
//		log.info("==username==" + currentPrincipalName);
//		Iterable<String> rolesInString = userRepository.findById(currentPrincipalName).get().getRoles();
//		Iterable<Role> roles = roleRepository.findAllById(rolesInString);
//		Set<String> menusInString = new HashSet<String>();
//		roles.forEach((role) -> menusInString.addAll(role.getAllowedMenus()));
////		log.info("==roles==" + roles);
//		Collection<Menu> menus = (Collection<Menu>) menuRepository.findAllById(menusInString);
////		log.info("==menus==" + menus);
		
		return menuService.getMenuByUser(currentPrincipalName);
	}
	
	
//	@PostMapping()
//	public User registerUser(@RequestBody User user) {
//		log.info("==register username==" + user.getUsername());
//		System.out.println("sysout ==register username==" + user.getUsername());
//		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//		return userRepository.save(user);
//	}
//	
//	@PatchMapping("/{id}")
//	public User editUser(@PathVariable(name="id")String username,@RequestBody User user) {
//		User user1 = userRepository.findById(username).get();
//		boolean passwordChanged = false;
//		
//				 // Existing user, check password in DB
//                String currentPassword = user1.getPassword();
//                
//                if (currentPassword == null) {
//                    passwordChanged = true;
//                } else {
//                    if (!currentPassword.equals(user.getPassword())) {
//                        passwordChanged = true;
//                    }
//                }
//                if(passwordChanged) {
//                		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//                }
//		return userRepository.save(user);
//	}
	
//	@PutMapping()
//	public User editUser(@RequestBody User user) {
//		log.info("==register username==" + user.getUsername());
//		System.out.println("sysout ==register username==" + user.getUsername());
//		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//		return userRepository.save(user);
//	}
	
//	@PutMapping("/{id}")
//	public List<User>editUserAll(@PathVariable(name="id"),@RequestBody User user) {
//		
//	}
	
	

}