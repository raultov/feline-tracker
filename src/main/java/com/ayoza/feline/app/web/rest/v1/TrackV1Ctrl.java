package com.ayoza.feline.app.web.rest.v1;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;
import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.common.ApiUser;
import ayoza.com.feline.api.entities.tracker.ApiTraPoint;
import ayoza.com.feline.api.entities.tracker.ApiTraRoute;
import ayoza.com.feline.api.exceptions.FelineApiException;
import ayoza.com.feline.api.exceptions.TrackerException;
import ayoza.com.feline.api.exceptions.UserServicesException;
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
    public ApiTraPoint addPointV1(
    								@RequestParam(value="latitude", required=true)  String ggaLatitude, // 4025.7313,N
    								@RequestParam(value="longitude", required=true)  String ggaLongitude, // 00338.5613,W
    								@RequestParam(value="accuracy", required=false)  Double accuracy,
    								@RequestParam(value="altitude", required=false)  Double altitude
    							) throws FelineApiException {
		
		ApiUser apiUser = accessControl.getUserFromSecurityContext();

		if (apiUser == null) {
			throw new UserServicesException(UserServicesException.ERROR_USER_NOT_FOUND, 
											UserServicesException.ERROR_USER_NOT_FOUND_MSG, 
											new Exception(UserServicesException.ERROR_USER_NOT_FOUND_MSG));
		}
		
		Date limit = DateUtils.addMinutes2FechaActual(-2);
		
		ApiTraRoute route = trackerMgr.getCurrentRoute(apiUser.getUserId(), limit);
		
		if (route == null) {
			route = trackerMgr.createRoute(apiUser.getUserId());
		}
		
		if (route == null) {
			throw new TrackerException(TrackerException.ERROR_TRACK_COULD_NOT_BE_CREATED, 
										TrackerException.ERROR_TRACK_COULD_NOT_BE_CREATED_MSG, 
										new Exception(TrackerException.ERROR_TRACK_COULD_NOT_BE_CREATED_MSG));
		}
		
		Double latitude = extractLatitude(ggaLatitude);
		Double longitude = extractLongitude(ggaLongitude);
		
		ApiTraPoint point = trackerMgr.addPointToRoute(route, latitude, longitude, accuracy, altitude);
		
    	return point;
	}
	
	private Double extractLatitude(String ggaLatitude) throws ParserTrackerException {
		
		Double degrees = 0.0, minutes = 0.0;
		
		try {
			degrees = new Double(ggaLatitude.substring(0, 2));
			minutes = new Double(ggaLatitude.substring(2, ggaLatitude.length()-1));
		} catch(NumberFormatException nfe) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_GPGGA_FORMAT,
												ParserTrackerException.WRONG_GPGGA_FORMAT_MSG,
												nfe);
		}
		
		double sign = ggaLatitude.charAt(ggaLatitude.length()-1) == 'N' ? 1.0 : -1.0;
		
		minutes = minutes / 60.0;
		
		return (degrees + minutes) * sign;
	}
	
	private Double extractLongitude(String ggaLongitude) throws ParserTrackerException {
		
		Double degrees = 0.0, minutes = 0.0;
		
		try {
			degrees = new Double(ggaLongitude.substring(0, 3));
			minutes = new Double(ggaLongitude.substring(3, ggaLongitude.length()-1));
		} catch(NumberFormatException | IndexOutOfBoundsException nfe) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_GPGGA_FORMAT,
												ParserTrackerException.WRONG_GPGGA_FORMAT_MSG,
												nfe);
		}
		
		double sign = ggaLongitude.charAt(ggaLongitude.length()-1) == 'E' ? 1.0 : -1.0;
		
		minutes = minutes / 60.0;
		
		return (degrees + minutes) * sign;
	}	

}


















