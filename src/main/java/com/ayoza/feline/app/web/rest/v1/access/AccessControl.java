package com.ayoza.feline.app.web.rest.v1.access;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ayoza.com.feline.api.entities.common.Identifiable;
import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.exceptions.UserServicesException;
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
        
		Authentication authentication = getAuthentication().orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());

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
	
	@SuppressWarnings("unchecked")
	public Integer getUserIdFromSecurityContext() {
		Authentication authentication = getAuthentication().orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
		
		Integer userId;
		if (authentication.getPrincipal() instanceof Identifiable) {
			
			Identifiable<Integer> user = (Identifiable<Integer>) authentication.getPrincipal();
    		userId = user.getId();
    	} else if (authentication.getPrincipal() instanceof String) {
			userId = userServicesMgr.getApiUserByUsername((String) authentication.getPrincipal())
					.map(UserDTO::getUserId)
					.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
		} else {
    		throw new SecurityException("Unexpected Principal Object");
    	}
		
		return userId;
	}
	
	private Optional<Authentication> getAuthentication() {
    	return ofNullable(SecurityContextHolder.getContext().getAuthentication()).filter(a -> a != null).filter(a -> a.getPrincipal() != null);
	}
}
