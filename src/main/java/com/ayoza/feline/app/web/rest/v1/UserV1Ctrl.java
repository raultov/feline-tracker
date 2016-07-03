package com.ayoza.feline.app.web.rest.v1;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ayoza.com.feline.api.entities.app.ApiAppUser;
import ayoza.com.feline.api.exceptions.FelineApiException;
import ayoza.com.feline.api.managers.UserServicesMgr;

@RestController
@RequestMapping(value = "/v1/users")
public class UserV1Ctrl {
	
	private UserServicesMgr userServicesMgr;

	public UserV1Ctrl(UserServicesMgr userServicesMgr) {
		this.userServicesMgr = userServicesMgr;
	}
	
	@RequestMapping(value = "", method = GET, produces = MediaType.APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public ApiAppUser getUserV1() throws FelineApiException {
		
		return null;
	}
}
