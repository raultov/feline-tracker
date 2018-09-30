package com.ayoza.feline.app.web.rest.v1.user;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Optional;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.AppUserMgr;
import ayoza.com.feline.api.user.dto.AppUserDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v1/users")
@RequiredArgsConstructor
public class UserV1Ctrl {
	
	private final AppUserMgr appUserMgr;
	private final AccessControl accessControl;
	
	@RequestMapping(value = "", method = GET, produces = MediaType.APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public AppUserDTO getAppUserV1() {
		Optional<AppUserDTO> appUserDTO = appUserMgr.getApiUserByEmail(accessControl.getUserIdFromSecurityContext());
		return appUserDTO.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
	}
}
