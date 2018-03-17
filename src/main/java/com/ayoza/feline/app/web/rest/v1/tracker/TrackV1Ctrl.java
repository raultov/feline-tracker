package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.app.web.rest.v1.tracker.TrackerUtils.convertToDecimalDegrees;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

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
import ayoza.com.feline.api.exceptions.FelineNoContentException;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v1/tracks")
@RequiredArgsConstructor
public class TrackV1Ctrl {
	
	static final String ORDER_BY_START_DATE = "startDate";

	private final RouteMgr routeMgr;
	
	private final PointMgr pointMgr;
	
	private final AccessControl accessControl;
	
	private final TrackValidator trackValidator;
	
	private final TrackService trackService;

	@Auditable
	@RequestMapping(value = "", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public void addPointV1(
								@RequestParam(value="latitude", required=true)  String ggaLatitude, // 4025.7313N
								@RequestParam(value="longitude", required=true)  String ggaLongitude, // 00338.5613W
								@RequestParam(value="accuracy", required=false)  Double accuracy,
								@RequestParam(value="altitude", required=false)  Double altitude
							) {
		
		PointDTO pointDTO = PointDTO.builder()
				.accuracy(accuracy)
				.altitude(altitude)
				.latitude(convertToDecimalDegrees(ggaLatitude))
				.longitude(convertToDecimalDegrees(ggaLongitude))
				.build();
		trackService.addPoint(pointDTO, accessControl.getUserIdFromSecurityContext());
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
	@RequestMapping(value = "/last", method = GET, produces = APPLICATION_JSON_VALUE, headers="Accept=*/*")
	@ResponseBody
	public RouteDTO getLastRouteV1(@RequestParam(value="trackerId", required=false) Integer trackerId) {
		int userId = accessControl.getUserIdFromSecurityContext();
		
		return routeMgr.getLastRoute(userId, ofNullable(trackerId))
				.orElseThrow(() -> FelineNoContentException.Exceptions.NO_CONTENT.getException());
	}
	
	@Auditable
	@RequestMapping(value = "/{trackId}/points", method = GET, produces = APPLICATION_JSON_VALUE, headers = "Accept=*/*")
	@ResponseBody
	public List<PointDTO> getListOfPointsByRouteV1(@PathVariable(value = "trackId") UUID trackId) {
		return pointMgr
				.getPointsByTraRouteIdAndAppUserId(trackId, accessControl.getUserIdFromSecurityContext(), empty());
	}
}


















