package com.ayoza.feline.app.web.rest.v1.user;

import static java.util.Optional.of;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.AppUserMgr;
import ayoza.com.feline.api.user.dto.AppUserDTO;

@RunWith(MockitoJUnitRunner.class)
public class UserV1CtrlTest {
	
	private static final String EMAIL = randomAlphanumeric(10);
	private static final Optional<AppUserDTO> APP_USER_DTO = of(AppUserDTO.builder().build());
	private static final Optional<AppUserDTO> EMPTY_APP_USER_DTO = Optional.empty();

	@Mock
	private AppUserMgr appUserMgr;
	
	@Mock
	private AccessControl accessControl;
	
	@InjectMocks
	private UserV1Ctrl userV1Ctrl;

	@Test
	public void validAppUser() throws Exception {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(EMAIL);
		when(appUserMgr.getApiUserByEmail(anyString())).thenReturn(APP_USER_DTO);
		
		AppUserDTO appUserDTO = userV1Ctrl.getAppUserV1();
		
		assertThat(appUserDTO).isEqualTo(APP_USER_DTO.get());
		verify(accessControl).getUserIdFromSecurityContext();
		verify(appUserMgr).getApiUserByEmail(anyString());
	}
	
	@Test(expected = UserServicesException.class)
	public void invalidAppUserEmptyAppUserDTO() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(EMAIL);
		when(appUserMgr.getApiUserByEmail(anyString())).thenReturn(EMPTY_APP_USER_DTO);
		
		userV1Ctrl.getAppUserV1();
	}
}
