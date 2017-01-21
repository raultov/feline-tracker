package com.ayoza.feline.app.web.rest.v1;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;
import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.exceptions.FelineApiException;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;

@RestController
@RequestMapping(value = "/v1/tracks")
public class TrackV1Ctrl {
	
	private static final double MIN_DIFF = 0.0002;
	
	private TrackerMgr trackerMgr;
	
	private AccessControl accessControl;
	
	@Autowired
	public TrackV1Ctrl(TrackerMgr trackerMgr,
						AccessControl accessControl) {
		this.trackerMgr = trackerMgr;
		this.accessControl = accessControl;
	}
	
	@RequestMapping(value = "", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public PointDTO addPointV1(
								@RequestParam(value="latitude", required=true)  String ggaLatitude, // 4025.7313,N
								@RequestParam(value="longitude", required=true)  String ggaLongitude, // 00338.5613,W
								@RequestParam(value="accuracy", required=false)  Double accuracy,
								@RequestParam(value="altitude", required=false)  Double altitude
    							) throws FelineApiException {
		
		UserDTO userDTO = accessControl.getUserFromSecurityContext()
										.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
		
		//Date limit = DateUtils.addMinutes2FechaActual(-2);
		Instant from = Instant.now().minus(2, ChronoUnit.MINUTES);
		
		RouteDTO routeDTO = trackerMgr.getCurrentRoute(userDTO.getUserId(), from)
							.orElseGet((() -> trackerMgr.createRoute(userDTO.getUserId())));
		
		Double latitude = extractLatitude(ggaLatitude);
		Double longitude = extractLongitude(ggaLongitude);
		
		Optional<PointDTO> lastPoint = trackerMgr.getLastPoint(routeDTO.getRouteId());
		Optional<PointDTO> updatedPoint = lastPoint.filter(t -> t.getLatitude() - latitude < MIN_DIFF)
				.filter(t -> t.getLongitude() - longitude < MIN_DIFF)
				.map(t -> {
					PointDTO pointDTO = PointDTO.builder()
							.accuracy(accuracy)
							.altitude(altitude)
							.latitude(latitude)
							.longitude(longitude)
							.when(Instant.now())
							.build();
					return trackerMgr.updatePoint(t.getPointId(), pointDTO);
				});

		PointDTO pointDTO = PointDTO.builder()
				.latitude(latitude)
				.longitude(longitude)
				.accuracy(accuracy)
				.altitude(altitude)
				.build();
		return updatedPoint.orElseGet(() -> trackerMgr.addPointToRoute(pointDTO, routeDTO));
	}
	
	@RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public List<RouteDTO> getListOfRoutesV1(
    											@RequestParam(value="startDateFrom") @DateTimeFormat(pattern = "yyyyMMddHHmmss") Calendar from,
    											@RequestParam(value="startDateTo")  @DateTimeFormat(pattern = "yyyyMMddHHmmss") Calendar to,
    											@RequestParam(value="orderAscDesc") String orderAscDesc,
    											@RequestParam(value="page") Integer page,
    											@RequestParam(value="numRegistersPerPage") Integer numRegistersPerPage
    					) throws FelineApiException {
		
		Optional<UserDTO> userDTO = accessControl.getUserFromSecurityContext();

		if (!userDTO.isPresent()) {
			throw UserServicesException.Exceptions.USER_NOT_FOUND.getException();
		}
		
		Instant startDateFrom = from.toInstant();
		Instant startDateTo = to.toInstant();
		
		validateGetTracks(startDateFrom, startDateTo,
							orderAscDesc,
								page, numRegistersPerPage);
		
		
		return trackerMgr.getRouteByApiTraUserAndFromStarDate(userDTO.get().getUserId(), 
																startDateFrom, startDateTo,
																orderAscDesc,
																page, numRegistersPerPage);
	}
	
	@RequestMapping(value = "/{trackId}/points", method = GET, produces = APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	@ResponseBody
	public List<PointDTO> getListOfPointsByRouteV1(@PathVariable(value = "trackId") Integer trackId)
			throws FelineApiException {

		Optional<UserDTO> userDTO = accessControl.getUserFromSecurityContext();

		if (!userDTO.isPresent()) {
			throw UserServicesException.Exceptions.USER_NOT_FOUND.getException();
		}

		return trackerMgr.getPointsByApiTraRouteIdAndUserId(trackId, userDTO.get().getUserId());
	}
	
	@RequestMapping(value = "/{trackId}/center", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public PointDTO getRouteV1(
    										@PathVariable(value="trackId") Integer trackId
    								) throws FelineApiException {
		
		Optional<UserDTO> userDTO = accessControl.getUserFromSecurityContext();

		if (!userDTO.isPresent()) {
			throw UserServicesException.Exceptions.USER_NOT_FOUND.getException();
		}
		
		List<PointDTO> list = trackerMgr.getPointsByApiTraRouteIdAndUserId(trackId, userDTO.get().getUserId());
		return getCentralApiTraPoint(list);
	}
	
	/*
		__     __    _ _     _       _   _                 
		\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  ___ 
		 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \/ __|
		  \ V / (_| | | | (_| | (_| | |_| | (_) | | | \__\
		   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|___/
		                                                   
	 */
	
	private void validateGetTracks(Instant startDateFrom, Instant startDateTo,
											String orderAscDesc,
												Integer page, Integer numRegistersPerPage) throws FelineApiException {
		
		if (startDateFrom != null && startDateTo != null) {
			if (startDateFrom.isAfter(startDateTo)) {
				throw new ParserTrackerException(ParserTrackerException.WRONG_DATES_VALUES, 
													ParserTrackerException.WRONG_DATES_VALUES_MSG, 
													new Exception(ParserTrackerException.WRONG_DATES_VALUES_MSG));
			}
		}
		
		if (orderAscDesc == null || orderAscDesc.isEmpty()) {
			throw new ParserTrackerException(ParserTrackerException.ERROR_ORDER_CANNOT_BE_EMPTY, 
													ParserTrackerException.ERROR_ORDER_CANNOT_BE_EMPTY_MSG, 
													new Exception(ParserTrackerException.ERROR_ORDER_CANNOT_BE_EMPTY_MSG));	
		} else if (!orderAscDesc.equals("ASC") && !orderAscDesc.equals("DESC")) {
				throw new ParserTrackerException(ParserTrackerException.WRONG_ORDER_VALUE, 
													ParserTrackerException.WRONG_ORDER_VALUE_MSG, 
													new Exception(ParserTrackerException.WRONG_ORDER_VALUE_MSG));
		}
		
		if (page == null) {
			throw new ParserTrackerException(ParserTrackerException.ERROR_PAGE_CANNOT_BE_EMPTY, 
													ParserTrackerException.ERROR_PAGE_CANNOT_BE_EMPTY_MSG, 
													new Exception(ParserTrackerException.ERROR_PAGE_CANNOT_BE_EMPTY_MSG));			
		} else if (page < 0) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_PAGE_NEGATIVE_VALUE, 
													ParserTrackerException.WRONG_PAGE_NEGATIVE_VALUE_MSG, 
													new Exception(ParserTrackerException.WRONG_PAGE_NEGATIVE_VALUE_MSG));
		}
		
		if (numRegistersPerPage == null) {
			throw new ParserTrackerException(ParserTrackerException.ERROR_REGISTERS_PER_PAGE_CANNOT_BE_EMPTY, 
													ParserTrackerException.ERROR_REGISTERS_PER_PAGE_CANNOT_BE_EMPTY_MSG, 
													new Exception(ParserTrackerException.ERROR_REGISTERS_PER_PAGE_CANNOT_BE_EMPTY_MSG));
		} else if (numRegistersPerPage < 0) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE, 
													ParserTrackerException.WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE_MSG, 
													new Exception(ParserTrackerException.WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE_MSG));
		}
	}
	
	/*
		 _   _ _   _ _ _ _   _           
		| | | | |_(_) (_) |_(_) ___  ___ 
		| | | | __| | | | __| |/ _ \/ __|
		| |_| | |_| | | | |_| |  __/\__ \
		 \___/ \__|_|_|_|\__|_|\___||___/
		                                 
	*/
	
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
	

	private static PointDTO getCentralApiTraPoint(List<PointDTO> geoCoordinates) {
		
		if (geoCoordinates == null || geoCoordinates.size() == 0) {
            return null;
        }
		
        if (geoCoordinates.size() == 1) {
            return geoCoordinates.get(0);
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (PointDTO apiTraPoint : geoCoordinates) {
            double latitude = apiTraPoint.getLatitude() * Math.PI / 180;
            double longitude = apiTraPoint.getLongitude() * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        int total = geoCoordinates.size();

        x = x / total;
        y = y / total;
        z = z / total;

        double centralLongitude = Math.atan2(y, x);
        double centralSquareRoot = Math.sqrt(x * x + y * y);
        double centralLatitude = Math.atan2(z, centralSquareRoot);
        
        PointDTO result = new PointDTO();
        result.setLatitude(centralLatitude * 180 / Math.PI);
        result.setLongitude(centralLongitude * 180 / Math.PI);

        return result;
    }

}


















