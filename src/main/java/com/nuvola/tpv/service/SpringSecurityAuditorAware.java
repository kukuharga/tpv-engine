package com.nuvola.tpv.service;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nuvola.tpv.model.User;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

	  public Optional<String> getCurrentAuditor() {

	    return Optional.ofNullable(SecurityContextHolder.getContext())
				  .map(SecurityContext::getAuthentication)
				  .filter(Authentication::isAuthenticated)
				  .map(Authentication::getPrincipal)
				  .map(String.class::cast);
	  }
	}
