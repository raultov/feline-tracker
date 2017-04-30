package com.ayoza.feline.app.web.rest.v1.access;

import static java.util.Optional.of;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ayoza.com.feline.api.entities.common.Identifiable;
import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.UserServicesMgr;

@RunWith(MockitoJUnitRunner.class)
public class AccessControlTest {
	
	private static final Authentication VALID_AUTHENTICATION = mock(Authentication.class);
	private static final SecurityContext SECURITY_CONTEXT = mock(SecurityContext.class);
	private static final UserDetails USER_DETAILS = mock(UserDetails.class);
	private static final Object UNEXPECTED_OBJECT = mock(Object.class);
	private static final Identifiable<?> USER = mock(Identifiable.class);
	private static final UserDTO USER_DTO = mock(UserDTO.class);
	
	private static final String USERNAME = "Test";
	
	@Mock
	private UserServicesMgr userServicesMgr;
	
	@InjectMocks
	private AccessControl accessControl;
	
	@Test
	public void validUserFromSecurityContextWithPrincipalAsString() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(new String(USERNAME));
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserFromSecurityContext();
		
		verify(userServicesMgr).getApiUserByUsername(USERNAME);
	}
	
	@Test
	public void validUserFromSecurityContextWithPrincipalAsUserDetails() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(USER_DETAILS);
		when(USER_DETAILS.getUsername()).thenReturn(USERNAME);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserFromSecurityContext();
		
		verify(USER_DETAILS).getUsername();
		verify(userServicesMgr).getApiUserByUsername(USERNAME);
	}
	
	@Test(expected = SecurityException.class)
	public void validUserFromSecurityContextWithPrincipalAsUnexpectedObject() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(UNEXPECTED_OBJECT);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserFromSecurityContext();
	}
	
	@Test(expected = UserServicesException.class)
	public void shouldThrowUserServicesExceptionWhenAuthenticationIsNull() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserFromSecurityContext();
	}
	
	@Test(expected = UserServicesException.class)
	public void invalidUserFromSecurityContextWithPrincipalNull() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(null);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserFromSecurityContext();
	}
	
	
	@Test
	public void getUserIdShouldReturnUserIdFromSecurityContextWhenPrincipalAsString() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(userServicesMgr.getApiUserByUsername(USERNAME)).thenReturn(of(USER_DTO));
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(new String(USERNAME));
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserIdFromSecurityContext();
		
		verify(userServicesMgr).getApiUserByUsername(USERNAME);
	}
	
	@Test
	public void getUserIdShouldReturnUserIdFromSecurityContextWhenPrincipalAsIdentifiable() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(USER);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);

		accessControl.getUserIdFromSecurityContext();
		
		verify(userServicesMgr, never()).getApiUserByUsername(USERNAME);
	}
	
	@Test(expected = SecurityException.class)
	public void getUserIdShouldThrowSecurityExceptionWhenPrincipalAsUnexpectedObject() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(UNEXPECTED_OBJECT);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserIdFromSecurityContext();
	}
	
	@Test(expected = UserServicesException.class)
	public void getUserIdShouldThrowUserServicesExceptionWhenAuthenticationIsNull() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserIdFromSecurityContext();
	}
	
	@Test(expected = UserServicesException.class)
	public void getUserIdShouldThrowUserServicesExceptionWhenPrincipalisNull() {
		when(SECURITY_CONTEXT.getAuthentication()).thenReturn(VALID_AUTHENTICATION);
		when(VALID_AUTHENTICATION.getPrincipal()).thenReturn(null);
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
		
		accessControl.getUserIdFromSecurityContext();
	}
}
