package com.ayoza.feline.app.web.rest.v1.user;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.audit.Auditable;
import ayoza.com.feline.api.entities.tracker.dto.TraUserDTO;
import ayoza.com.feline.api.managers.TraUserMgr;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping(value = "/v1/trackers")
@AllArgsConstructor(onConstructor=@__({@Autowired}))
@FieldDefaults(level=AccessLevel.PRIVATE)
public class TrackersV1Ctrl {
	
	private AccessControl accessControl;
	private TraUserMgr traUserMgr;

	@Auditable
	@RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
	public List<TraUserDTO> getTrackers() {
		return traUserMgr.getTraUsersByAppUserId(accessControl.getUserIdFromSecurityContext());
	}

}
