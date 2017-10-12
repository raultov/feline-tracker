package com.ayoza.feline.app.web.rest.v1.user;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.app.dto.AppUserDTO;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.AppUserMgr;

@RunWith(MockitoJUnitRunner.class)
public class UserV1CtrlTest {
	
	private static final int USER_ID = 1;
	private static final Optional<AppUserDTO> APP_USER_DTO = of(mock(AppUserDTO.class));
	private static final Optional<AppUserDTO> EMPTY_APP_USER_DTO = Optional.empty();

	@Mock
	private AppUserMgr appUserMgr;
	
	@Mock
	private AccessControl accessControl;
	
	@InjectMocks
	private UserV1Ctrl userV1Ctrl;

	@Test
	public void validAppUser() throws Exception {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(appUserMgr.getApiUserByUserId(anyInt())).thenReturn(APP_USER_DTO);
		
		AppUserDTO appUserDTO = userV1Ctrl.getAppUserV1();
		
		assertThat(appUserDTO).isEqualTo(APP_USER_DTO.get());
		verify(accessControl).getUserIdFromSecurityContext();
		verify(appUserMgr).getApiUserByUserId(anyInt());
	}
	
	@Test(expected = UserServicesException.class)
	public void invalidAppUserEmptyAppUserDTO() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(appUserMgr.getApiUserByUserId(anyInt())).thenReturn(EMPTY_APP_USER_DTO);
		
		userV1Ctrl.getAppUserV1();
	}
}
