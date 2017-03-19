package com.ayoza.feline.app.web.rest.v1.access;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.managers.UserServicesMgr;

@RunWith(MockitoJUnitRunner.class)
public class AccessControlTest {
	
	private static final Authentication validAuthentication = mock(Authentication.class);
	private static final SecurityContext securityContext = mock(SecurityContext.class);
	private static final UserDetails userDetails = mock(UserDetails.class);
	private static final Object unexpectedObject = mock(Object.class);
	
	private static final String USERNAME = "Test";
	
	@Mock
	private UserServicesMgr userServicesMgr;
	
	@InjectMocks
	private AccessControl accessControl;
	
	@Test
	public void validUserFromSecurityContextWithPrincipalAsString() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(new String(USERNAME));
		SecurityContextHolder.setContext(securityContext);
		
		accessControl.getUserFromSecurityContext();
		
		verify(userServicesMgr).getApiUserByUsername(USERNAME);
	}
	
	@Test
	public void validUserFromSecurityContextWithPrincipalAsUserDetails() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(userDetails);
		when(userDetails.getUsername()).thenReturn(USERNAME);
		SecurityContextHolder.setContext(securityContext);
		
		accessControl.getUserFromSecurityContext();
		
		verify(userDetails).getUsername();
		verify(userServicesMgr).getApiUserByUsername(USERNAME);
	}
	
	@Test(expected = SecurityException.class)
	public void validUserFromSecurityContextWithPrincipalAsUnexpectedObject() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(unexpectedObject);
		SecurityContextHolder.setContext(securityContext);
		
		accessControl.getUserFromSecurityContext();
		
		verify(userServicesMgr, never()).getApiUserByUsername(USERNAME);
	}
	
	@Test
	public void invalidUserFromSecurityContextWithAuthenticationNull() {
		when(securityContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);
		
		Optional<UserDTO> userDTO = accessControl.getUserFromSecurityContext();
		
		assertThat(userDTO).isEmpty();
		verify(userServicesMgr, never()).getApiUserByUsername(USERNAME);
	}
	
	@Test
	public void invalidUserFromSecurityContextWithPrincipalNull() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);
		
		Optional<UserDTO> userDTO = accessControl.getUserFromSecurityContext();
		
		assertThat(userDTO).isEmpty();
		verify(userServicesMgr, never()).getApiUserByUsername(USERNAME);
	}
}
