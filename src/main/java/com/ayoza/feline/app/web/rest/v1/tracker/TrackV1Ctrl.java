package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.app.web.rest.v1.tracker.TrackerUtils.convertToDecimalDegrees;
import static com.ayoza.feline.app.web.rest.v1.tracker.TrackerUtils.getCentralApiTraPoint;
import static java.lang.Math.abs;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static com.ayoza.feline.app.web.rest.v1.cache.CacheV1Ctrl.POINTS_CACHE;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.audit.Auditable;
import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;
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
	
	RouteMgr routeMgr;
	
	PointMgr pointMgr;
	
	AccessControl accessControl;
	
	TrackValidator trackValidator;

	@CacheEvict(value=POINTS_CACHE, key="result.routeId")
	@Auditable
	@RequestMapping(value = "", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public PointDTO addPointV1(
								@RequestParam(value="latitude", required=true)  String ggaLatitude, // 4025.7313N
								@RequestParam(value="longitude", required=true)  String ggaLongitude, // 00338.5613W
								@RequestParam(value="accuracy", required=false)  Double accuracy,
								@RequestParam(value="altitude", required=false)  Double altitude
							) {
		
		Instant from = Instant.now().minus(2, ChronoUnit.MINUTES);
		
		int userId = accessControl.getUserIdFromSecurityContext();
		RouteDTO routeDTO = routeMgr.getCurrentRoute(userId, from)
							.orElseGet((() -> routeMgr.createRoute(userId)));
		
		Double latitude = convertToDecimalDegrees(ggaLatitude);
		Double longitude = convertToDecimalDegrees(ggaLongitude);
		
		Optional<PointDTO> lastPoint = pointMgr.getLastPoint(routeDTO.getRouteId());

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
					return pointMgr.updatePoint(t.getPointId(), pointDTO);
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
	
	@Auditable
	@RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public List<RouteDTO> getListOfRoutesV1(	@RequestParam(value="trackerId", required=false) Integer trackerId,
    											@RequestParam(value="startDateFrom", required=false) @DateTimeFormat(pattern = "yyyyMMddHHmmss") Calendar from,
    											@RequestParam(value="startDateTo", required=false)  @DateTimeFormat(pattern = "yyyyMMddHHmmss") Calendar to,
    											@RequestParam(value="orderAscDesc") String orderAscDesc,
    											@RequestParam(value="page") Integer page,
    											@RequestParam(value="numRegistersPerPage") Integer numRegistersPerPage
    					) {
		
		int userId = accessControl.getUserIdFromSecurityContext();
		
		Instant startDateFrom = ofNullable(from).map(Calendar::toInstant).orElse(null);
		Instant startDateTo = ofNullable(to).map(Calendar::toInstant).orElse(null);
		
		trackValidator.validateGetTracks(startDateFrom, startDateTo,
											orderAscDesc,
												page, numRegistersPerPage);
		
		PageRequest pageRequest = new PageRequest(page, numRegistersPerPage, Sort.Direction.valueOf(orderAscDesc), ORDER_BY_START_DATE);
		
		return routeMgr.getRoutes(userId,
									ofNullable(trackerId),
										startDateFrom, startDateTo,
											pageRequest);
	}
	
	@Auditable
	@Cacheable(value=POINTS_CACHE, key="#p0")
	@RequestMapping(value = "/{trackId}/points", method = GET, produces = APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	@ResponseBody
	public List<PointDTO> getListOfPointsByRouteV1(@PathVariable(value = "trackId") Integer trackId) {
		return pointMgr.getPointsByTraRouteIdAndAppUserId(trackId, accessControl.getUserIdFromSecurityContext());
	}
	
	@Auditable
	@Cacheable(value=POINTS_CACHE, key="#p0")
	@RequestMapping(value = "/{trackId}/center", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
    @ResponseBody
    public PointDTO getCentralPointV1(@PathVariable(value="trackId") Integer trackId) {
		return getCentralApiTraPoint(pointMgr.getPointsByTraRouteIdAndAppUserId(trackId, accessControl.getUserIdFromSecurityContext()));
	}
}


















