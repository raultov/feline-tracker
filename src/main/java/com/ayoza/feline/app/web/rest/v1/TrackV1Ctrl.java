package com.ayoza.feline.app.web.rest.v1;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import ayoza.com.feline.api.entities.tracker.ApiTraRoute;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;

@RestController
@RequestMapping(value = "/v1/tracks")
public class TrackV1Ctrl {
	
	private TrackerMgr trackerMgr;
	
	@Autowired
	public TrackV1Ctrl(TrackerMgr trackerMgr) {
		this.trackerMgr = trackerMgr;
	}
	
	@RequestMapping(value = "", method = POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public ApiTraRoute addPointV1() {
    	
		ApiTraRoute route = trackerMgr.createRoute(1);

    	return route;
	}

}
