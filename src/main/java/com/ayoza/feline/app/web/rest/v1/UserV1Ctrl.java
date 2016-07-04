package com.ayoza.feline.app.web.rest.v1;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.entities.app.ApiAppUser;
import ayoza.com.feline.api.entities.common.ApiUser;
import ayoza.com.feline.api.exceptions.FelineApiException;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.UserServicesMgr;

@RestController
@RequestMapping(value = "/v1/users")
public class UserV1Ctrl {
	
	private UserServicesMgr userServicesMgr;
	private AccessControl accessControl;

	@Autowired
	public UserV1Ctrl(UserServicesMgr userServicesMgr,
						AccessControl accessControl) {
		this.userServicesMgr = userServicesMgr;
		this.accessControl = accessControl;
	}
	
	@RequestMapping(value = "", method = GET, produces = MediaType.APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public ApiAppUser getAppUserV1() throws FelineApiException {
		
		ApiUser apiUser = accessControl.getUserFromSecurityContext();
		
		ApiAppUser apiAppUser = userServicesMgr.getApiUserByUserId(apiUser.getUserId());
		if (apiAppUser == null) {
			throw new UserServicesException(UserServicesException.ERROR_USER_NOT_FOUND, 
											UserServicesException.ERROR_USER_NOT_FOUND_MSG, 
											new Exception(UserServicesException.ERROR_USER_NOT_FOUND_MSG));
		}
		
		return apiAppUser;
	}
}
