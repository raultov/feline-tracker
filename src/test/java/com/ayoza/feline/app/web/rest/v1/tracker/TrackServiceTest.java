package com.ayoza.feline.app.web.rest.v1.tracker;

import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Optional.empty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;

@RunWith(MockitoJUnitRunner.class)
public class TrackServiceTest {
	
	private final static int USER_ID = 1;
	private final static int ROUTE_ID = 1;
	private final static long LAST_POINT_ID = 1L;

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

	private final static RouteDTO ROUTE_DTO = RouteDTO.builder().routeId(ROUTE_ID).build();
	
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
		when(routeMgr.getLastRouteFrom(eq(USER_ID), any())).thenReturn(of(ROUTE_DTO));
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(of(LAST_FARAWAY_POINT_DTO));
		
		trackService.addPoint(POINT_DTO, USER_ID);

		verify(routeMgr, never()).createRoute(anyInt());
		verify(pointMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr).addPointToRoute(any(), any());
	}
	
	
	@Test
	public void givenExistingLastPoint_whenAddPoint_thenUpdateLastPoint() {
		when(routeMgr.getLastRouteFrom(eq(USER_ID), any())).thenReturn(of(ROUTE_DTO));
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(of(LAST_CLOSE_POINT_DTO));
		when(pointMgr.updatePoint(anyLong(), any())).thenReturn(LAST_CLOSE_POINT_DTO);
		
		trackService.addPoint(POINT_DTO, USER_ID);

		verify(routeMgr, never()).createRoute(anyInt());
		verify(pointMgr).updatePoint(anyLong(), any());
		verify(trackerMgr, never()).addPointToRoute(any(), any());
	}
	
	@Test
	public void givenNonExistingLastPoint_whenAddPoint_thenCreateNewPoint() {
		when(routeMgr.getLastRouteFrom(eq(USER_ID), any())).thenReturn(of(ROUTE_DTO));
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(empty());
		
		trackService.addPoint(POINT_DTO, USER_ID);

		verify(routeMgr, never()).createRoute(anyInt());
		verify(pointMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr).addPointToRoute(POINT_DTO, ROUTE_DTO);
	}
	
	@Test
	public void givenNoPreviousRoute_whenAddPoint_thenCreateNewRouteAndNewPoint() {
		when(routeMgr.getLastRouteFrom(anyInt(), any())).thenReturn(empty());
		when(routeMgr.createRoute(anyInt())).thenReturn(ROUTE_DTO);
		when(pointMgr.getLastPoint(ROUTE_ID)).thenReturn(empty());
		
		trackService.addPoint(POINT_DTO, USER_ID);
		
		verify(routeMgr).createRoute(anyInt());
		verify(pointMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr).addPointToRoute(any(), any());
	}

	private static PointDTO forgePointDTO(Long pointId) {
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
