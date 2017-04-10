package com.ayoza.feline.app.web.rest.v1.user;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.app.dto.AppUserDTO;
import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.UserServicesMgr;

@RunWith(MockitoJUnitRunner.class)
public class UserV1CtrlTest {
	
	private static final Optional<UserDTO> USER_DTO = of(mock(UserDTO.class));
	private static final Optional<AppUserDTO> APP_USER_DTO = of(mock(AppUserDTO.class));
	private static final Optional<UserDTO> EMPTY_USER_DTO = Optional.empty();
	private static final Optional<AppUserDTO> EMPTY_APP_USER_DTO = Optional.empty();

	@Mock
	private UserServicesMgr userServicesMgr;
	
	@Mock
	private AccessControl accessControl;
	
	@InjectMocks
	private UserV1Ctrl userV1Ctrl;

	@Test
	public void validAppUser() throws Exception {
		when(accessControl.getUserFromSecurityContext()).thenReturn(USER_DTO);
		when(userServicesMgr.getApiUserByUserId(any())).thenReturn(APP_USER_DTO);
		
		AppUserDTO appUserDTO = userV1Ctrl.getAppUserV1();
		
		assertThat(appUserDTO).isEqualTo(APP_USER_DTO.get());
		verify(accessControl).getUserFromSecurityContext();
		verify(userServicesMgr).getApiUserByUserId(any());
	}
	
	@Test(expected = UserServicesException.class)
	public void invalidAppUserEmptyUserDTO() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(EMPTY_USER_DTO);
		
		userV1Ctrl.getAppUserV1();
		
		verify(accessControl).getUserFromSecurityContext();
		verify(userServicesMgr, never()).getApiUserByUserId(any());
	}
	
	@Test(expected = UserServicesException.class)
	public void invalidAppUserEmptyAppUserDTO() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(USER_DTO);
		when(userServicesMgr.getApiUserByUserId(any())).thenReturn(EMPTY_APP_USER_DTO);
		
		userV1Ctrl.getAppUserV1();
		
		verify(accessControl).getUserFromSecurityContext();
		verify(userServicesMgr).getApiUserByUserId(any());
	}
}
