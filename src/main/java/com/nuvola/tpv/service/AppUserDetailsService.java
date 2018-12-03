package com.nuvola.tpv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.model.UserGroup;
import com.nuvola.tpv.repo.UserGroupRepository;
import com.nuvola.tpv.repo.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class AppUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserGroupRepository groupRepository;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(s);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("The username %s doesn't exist", s));
		}

		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});

		user.getGroups().forEach(group -> {
			Optional<UserGroup> ug = groupRepository.findById(group);
			ug.ifPresent(ugObj -> {
				ugObj.getRoles().forEach(role -> {
					authorities.add(new SimpleGrantedAuthority(role));
				});
			});

		});

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), authorities);

		return userDetails;
	}

}
