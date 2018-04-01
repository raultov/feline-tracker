package com.ayoza.feline.app.web.rest.v1.access;

import static java.util.Optional.ofNullable;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ayoza.com.feline.api.exceptions.UserServicesException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessControl {

	public String getUserIdFromSecurityContext() {
		Object principal = getAuthentication().orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
		
		String username;
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
    		username = user.getUsername();
    	} else if (principal instanceof String) {
			username = (String) principal;
		} else {
    		throw new SecurityException("Unexpected Principal Object");
    	}
		
		return username;
	}
	
	private Optional<Object> getAuthentication() {
    	return ofNullable(SecurityContextHolder.getContext().getAuthentication())
    			.filter(Objects::nonNull)
    			.map(Authentication::getPrincipal)
    			.filter(Objects::nonNull);
	}
}
