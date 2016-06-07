package com.ayoza.feline.app.web.rest.v1;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.entities.common.ApiUser;
import ayoza.com.feline.api.entities.tracker.ApiTraRoute;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;
import ayoza.com.feline.api.utils.DateUtils;

@RestController
@RequestMapping(value = "/v1/tracks")
public class TrackV1Ctrl {
	
	private TrackerMgr trackerMgr;
	
	private AccessControl accessControl;
	
	@Autowired
	public TrackV1Ctrl(TrackerMgr trackerMgr,
						AccessControl accessControl) {
		this.trackerMgr = trackerMgr;
		this.accessControl = accessControl;
	}
	
	@RequestMapping(value = "", method = POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public ApiTraRoute addPointV1() {
		
		ApiUser apiUser = accessControl.getUserFromSecurityContext();
		
		Date limit = DateUtils.addMinutes2FechaActual(-1);
		
		ApiTraRoute route = trackerMgr.getCurrentRoute(apiUser.getUserId(), limit);
		
		if (route == null) {
			route = trackerMgr.createRoute(apiUser.getUserId());
		}
		
		// TODO Add point to route
		

    	return route;
	}

}
