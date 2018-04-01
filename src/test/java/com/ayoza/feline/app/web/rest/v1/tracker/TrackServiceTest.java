package com.ayoza.feline.app.web.rest.v1.tracker;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;

@RunWith(MockitoJUnitRunner.class)
public class TrackServiceTest {
	
	private final String simPhone = randomNumeric(10);
	private final static UUID ROUTE_ID = UUID.randomUUID();
	private final static UUID LAST_POINT_ID = UUID.randomUUID();

	private final static double LAST_LATITUDE_FARAWAY = 27.0;
	private final static double LAST_LONGITUDE_FARAWAY = -3.0;

	private final static double LATITUDE = 40.428855;
	private final static double LONGITUDE = -3.6426883333333335;
	private final static double ACCURACY = 2.0;
	private final static double ALTITUDE = 65.0;
	
	private final static double LAST_CLOSE_LATITUDE = LATITUDE;
	private final static double LAST_CLOSE_LONGITUDE = LONGITUDE;

	private final static PointDTO POINT_DTO = forgePointDTO(null);
	private final static PointDTO LAST_FARAWAY_POINT_DTO = forgeLastFarawayPointDTO();
	private final static PointDTO LAST_CLOSE_POINT_DTO = forgeLastClosePointDTO();

	private final static RouteDTO ROUTE_DTO = RouteDTO.builder().trackId(ROUTE_ID).build();
	
	@Mock
	private RouteMgr routeMgr;
	
	@Mock
	private PointMgr pointMgr;
	
	@Mock
	private TrackerMgr trackerMgr;
	
	@InjectMocks
	private TrackService trackService;
	
	@Test
	public void givenExistingLastPoint_whenAddPoint_thenCreateNewPoint() {
		when(routeMgr.getLastRouteFrom(eq(simPhone), any())).thenReturn(of(ROUTE_DTO));
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(of(LAST_FARAWAY_POINT_DTO));
		
		trackService.addPoint(POINT_DTO, simPhone);

		verify(routeMgr, never()).createRoute(anyString());
		verify(pointMgr, never()).updatePoint(any(UUID.class), any());
		verify(trackerMgr).addPointToRoute(any(), any(), any(Instant.class));
		verify(routeMgr).updateRoute(any(UUID.class), any(RouteDTO.class));
	}
	
	
	@Test
	public void givenExistingLastPoint_whenAddPoint_thenUpdateLastPoint() {
		when(routeMgr.getLastRouteFrom(eq(simPhone), any())).thenReturn(of(ROUTE_DTO));
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(of(LAST_CLOSE_POINT_DTO));
		when(pointMgr.updatePoint(any(UUID.class), any())).thenReturn(LAST_CLOSE_POINT_DTO);
		
		trackService.addPoint(POINT_DTO, simPhone);

		verify(routeMgr, never()).createRoute(anyString());
		verify(pointMgr).updatePoint(any(UUID.class), any());
		verify(trackerMgr, never()).addPointToRoute(any(), any(), any(Instant.class));
		verify(routeMgr).updateRoute(any(UUID.class), any(RouteDTO.class));
	}
	
	@Test
	public void givenNonExistingLastPoint_whenAddPoint_thenCreateNewPoint() {
		when(routeMgr.getLastRouteFrom(eq(simPhone), any())).thenReturn(of(ROUTE_DTO));
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(empty());
		
		trackService.addPoint(POINT_DTO, simPhone);

		verify(routeMgr, never()).createRoute(anyString());
		verify(pointMgr, never()).updatePoint(any(UUID.class), any());
		verify(trackerMgr).addPointToRoute(eq(POINT_DTO), eq(ROUTE_DTO), any(Instant.class));
		verify(routeMgr).updateRoute(any(UUID.class), any(RouteDTO.class));
	}
	
	@Test
	public void givenNoPreviousRoute_whenAddPoint_thenCreateNewRouteAndNewPoint() {
		when(routeMgr.getLastRouteFrom(anyString(), any())).thenReturn(empty());
		when(routeMgr.createRoute(anyString())).thenReturn(ROUTE_DTO);
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(empty());
		
		trackService.addPoint(POINT_DTO, simPhone);
		
		verify(pointMgr, never()).updatePoint(any(UUID.class), any());
		verify(trackerMgr).addPointToRoute(any(), any(), any(Instant.class));
		verify(routeMgr).updateRoute(any(UUID.class), any(RouteDTO.class));
	}

	private static PointDTO forgePointDTO(UUID pointId) {
		return PointDTO.builder()
				.pointId(pointId)
				.latitude(LATITUDE)
				.longitude(LONGITUDE)
				.accuracy(ACCURACY)
				.altitude(ALTITUDE)
				.build();
	}

	private static PointDTO forgeLastFarawayPointDTO() {
		return PointDTO.builder()
				.pointId(LAST_POINT_ID)
				.latitude(LAST_LATITUDE_FARAWAY)
				.longitude(LAST_LONGITUDE_FARAWAY)
				.build();
	}
	
	private static PointDTO forgeLastClosePointDTO() {
		return PointDTO.builder()
				.pointId(LAST_POINT_ID)
				.latitude(LAST_CLOSE_LATITUDE)
				.longitude(LAST_CLOSE_LONGITUDE)
				.build();
	}
}
