package com.ayoza.feline.app.web.rest.v1.access;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.managers.UserServicesMgr;

@Service
public class AccessControl {
	
	private UserServicesMgr userServicesMgr;
	
	@Autowired
	public AccessControl(UserServicesMgr userServicesMgr) {
		this.userServicesMgr = userServicesMgr;
	}

	public Optional<UserDTO> getUserFromSecurityContext() {
        
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
    	if ((authentication == null) || (authentication.getPrincipal() == null)) {
    		return Optional.empty();
    	}
		
    	String username = null;
    	if (authentication.getPrincipal() instanceof String) {
    		username = (String) authentication.getPrincipal();
    	} else {
    		final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    		username = userDetails.getUsername();
    	}
        
    	UserDTO userDTO;
		try {
			userDTO = userServicesMgr.getApiUserByUsername(username);
		} catch (UsernameNotFoundException e) {
			return Optional.empty();
		}
		
        return Optional.of(userDTO);
    }
	
}
