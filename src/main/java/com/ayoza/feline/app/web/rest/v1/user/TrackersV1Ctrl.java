package com.ayoza.feline.app.web.rest.v1.user;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ayoza.com.feline.api.audit.Auditable;
import ayoza.com.feline.api.managers.TrackerUserMgr;
import ayoza.com.feline.api.user.dto.TrackerUserDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v1/trackers")
@RequiredArgsConstructor
public class TrackersV1Ctrl {
	
	private final AccessControl accessControl;
	private final TrackerUserMgr trackerUserMgr;

	@Auditable
	@RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
	public List<TrackerUserDTO> getTrackersV1() {
		return trackerUserMgr.getTrackerUsersByEmail(accessControl.getUserIdFromSecurityContext());
	}

	@Auditable
	@RequestMapping(value = "/default", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
	public TrackerUserDTO getDefaultTrackerV1() {
		return trackerUserMgr.getDefaultTrackerUserByEmail(accessControl.getUserIdFromSecurityContext());
	}
}
