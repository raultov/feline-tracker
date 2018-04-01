package com.ayoza.feline.app.web.rest.v1.user;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.audit.Auditable;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.AppUserMgr;
import ayoza.com.feline.api.user.dto.AppUserDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping(value = "/v1/users")
@AllArgsConstructor(onConstructor=@__({@Autowired}))
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserV1Ctrl {
	
	private AppUserMgr appUserMgr;
	private AccessControl accessControl;
	
	@Auditable
	@RequestMapping(value = "", method = GET, produces = MediaType.APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public AppUserDTO getAppUserV1() {
		Optional<AppUserDTO> appUserDTO = appUserMgr.getApiUserByEmail(accessControl.getUserIdFromSecurityContext());
		return appUserDTO.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
	}
}
