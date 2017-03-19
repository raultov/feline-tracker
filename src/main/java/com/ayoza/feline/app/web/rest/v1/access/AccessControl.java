package com.ayoza.feline.app.web.rest.v1.access;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.managers.UserServicesMgr;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor(onConstructor=@__({@Autowired}))
@FieldDefaults(level=AccessLevel.PRIVATE)
public class AccessControl {
	
	UserServicesMgr userServicesMgr;

	public Optional<UserDTO> getUserFromSecurityContext() {
        
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
    	if ((authentication == null) || (authentication.getPrincipal() == null)) {
    		return Optional.empty();
    	}
		
    	String username = null;
    	if (authentication.getPrincipal() instanceof String) {
    		username = (String) authentication.getPrincipal();
    	} else if (authentication.getPrincipal() instanceof UserDetails) {
    		final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    		username = userDetails.getUsername();
    	} else {
    		throw new SecurityException("Unexpected Principal Object");
    	}
		
        return userServicesMgr.getApiUserByUsername(username);
    }
	
}
