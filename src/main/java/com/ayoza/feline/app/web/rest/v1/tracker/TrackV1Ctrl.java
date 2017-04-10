package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.app.web.rest.v1.tracker.TrackerUtils.convertToDecimalDegrees;
import static com.ayoza.feline.app.web.rest.v1.tracker.TrackerUtils.getCentralApiTraPoint;
import static java.lang.Math.abs;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping(value = "/v1/tracks")
@AllArgsConstructor(onConstructor=@__({@Autowired}))
@FieldDefaults(level=AccessLevel.PRIVATE)
public class TrackV1Ctrl {
	
	static final double MIN_DIFF = 0.0002;
	static final String ORDER_BY_START_DATE = "startDate";
	
	TrackerMgr trackerMgr;
	
	AccessControl accessControl;
	
	TrackValidator trackValidator;

	@RequestMapping(value = "", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public PointDTO addPointV1(
								@RequestParam(value="latitude", required=true)  String ggaLatitude, // 4025.7313N
								@RequestParam(value="longitude", required=true)  String ggaLongitude, // 00338.5613W
								@RequestParam(value="accuracy", required=false)  Double accuracy,
								@RequestParam(value="altitude", required=false)  Double altitude
							) {
		
		UserDTO userDTO = accessControl.getUserFromSecurityContext()
										.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
		
		Instant from = Instant.now().minus(2, ChronoUnit.MINUTES);
		
		RouteDTO routeDTO = trackerMgr.getCurrentRoute(userDTO.getUserId(), from)
							.orElseGet((() -> trackerMgr.createRoute(userDTO.getUserId())));
		
		Double latitude = convertToDecimalDegrees(ggaLatitude);
		Double longitude = convertToDecimalDegrees(ggaLongitude);
		
		Optional<PointDTO> lastPoint = trackerMgr.getLastPoint(routeDTO.getRouteId());

		Optional<PointDTO> updatedPoint = lastPoint
				.filter(t -> abs(t.getLatitude() - latitude) < MIN_DIFF)
				.filter(t -> abs(t.getLongitude() - longitude) < MIN_DIFF)
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

		return updatedPoint.orElseGet(() -> {
					PointDTO pointDTO = PointDTO.builder()
							.latitude(latitude)
							.longitude(longitude)
							.accuracy(accuracy)
							.altitude(altitude)
							.build();
			
					return trackerMgr.addPointToRoute(pointDTO, routeDTO);
				});
	}
	
	@RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public List<RouteDTO> getListOfRoutesV1(
    											@RequestParam(value="startDateFrom") @DateTimeFormat(pattern = "yyyyMMddHHmmss") Calendar from,
    											@RequestParam(value="startDateTo")  @DateTimeFormat(pattern = "yyyyMMddHHmmss") Calendar to,
    											@RequestParam(value="orderAscDesc") String orderAscDesc,
    											@RequestParam(value="page") Integer page,
    											@RequestParam(value="numRegistersPerPage") Integer numRegistersPerPage
    					) {
		
		UserDTO userDTO = accessControl.getUserFromSecurityContext()
				.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());
		
		Instant startDateFrom = from.toInstant();
		Instant startDateTo = to.toInstant();
		
		trackValidator.validateGetTracks(startDateFrom, startDateTo,
											orderAscDesc,
												page, numRegistersPerPage);
		
		PageRequest pageRequest = new PageRequest(page, numRegistersPerPage, Sort.Direction.valueOf(orderAscDesc), ORDER_BY_START_DATE);
		
		return trackerMgr.getRouteByTraUserAndFromStartDate(userDTO.getUserId(), 
																startDateFrom, startDateTo,
																pageRequest);
	}
	
	@RequestMapping(value = "/{trackId}/points", method = GET, produces = APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	@ResponseBody
	public List<PointDTO> getListOfPointsByRouteV1(@PathVariable(value = "trackId") Integer trackId) {

		UserDTO userDTO = accessControl.getUserFromSecurityContext()
				.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());

		return trackerMgr.getPointsByTraRouteIdAndAppUserId(trackId, userDTO.getUserId());
	}
	
	@RequestMapping(value = "/{trackId}/center", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public PointDTO getCentralPointV1(
    										@PathVariable(value="trackId") Integer routeId
    								) {
		
		UserDTO userDTO = accessControl.getUserFromSecurityContext()
				.orElseThrow(() -> UserServicesException.Exceptions.USER_NOT_FOUND.getException());

		List<PointDTO> list = trackerMgr.getPointsByTraRouteIdAndAppUserId(routeId, userDTO.getUserId());
		return getCentralApiTraPoint(list);
	}
}


















