package com.ayoza.feline.app.web.rest.v1.access;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ayoza.com.feline.api.exceptions.UserServicesException;

@RunWith(MockitoJUnitRunner.class)
public class AccessControlTest {
	
	private final Authentication validAuthentication = mock(Authentication.class);
	private final SecurityContext securityContext = mock(SecurityContext.class);
	private final Object unexpectedObject = mock(Object.class);
	private final UserDetails user = mock(UserDetails.class);
	
	private final String username = randomAlphanumeric(10);
	
	@InjectMocks
	private AccessControl accessControl;	
	
	@Test
	public void getUserIdShouldReturnUserIdFromSecurityContextWhenPrincipalAsString() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(new String(username));
		SecurityContextHolder.setContext(securityContext);
		
		String userId = accessControl.getUserIdFromSecurityContext();
		
		assertThat(userId).isNotBlank().isEqualTo(username);
	}
	
	@Test
	public void getUserIdShouldReturnUserIdFromSecurityContextWhenPrincipalAsUserDetails() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(user);
		when(user.getUsername()).thenReturn(username);
		SecurityContextHolder.setContext(securityContext);

		String userId = accessControl.getUserIdFromSecurityContext();
		
		assertThat(userId).isNotBlank().isEqualTo(username);
	}
	
	@Test(expected = SecurityException.class)
	public void getUserIdShouldThrowSecurityExceptionWhenPrincipalAsUnexpectedObject() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(unexpectedObject);
		SecurityContextHolder.setContext(securityContext);
		
		accessControl.getUserIdFromSecurityContext();
	}
	
	@Test(expected = UserServicesException.class)
	public void getUserIdShouldThrowUserServicesExceptionWhenAuthenticationIsNull() {
		when(securityContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);
		
		accessControl.getUserIdFromSecurityContext();
	}
	
	@Test(expected = UserServicesException.class)
	public void getUserIdShouldThrowUserServicesExceptionWhenPrincipalisNull() {
		when(securityContext.getAuthentication()).thenReturn(validAuthentication);
		when(validAuthentication.getPrincipal()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);
		
		accessControl.getUserIdFromSecurityContext();
	}
}
