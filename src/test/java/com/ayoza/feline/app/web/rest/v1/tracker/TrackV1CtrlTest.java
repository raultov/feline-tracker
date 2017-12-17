package com.ayoza.feline.app.web.rest.v1.tracker;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;
import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.exceptions.FelineNoContentException;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;

@RunWith(MockitoJUnitRunner.class)
public class TrackV1CtrlTest {
	
	private final static int USER_ID = 1; 
	private final static int ROUTE_ID = 1;
	private final static int TRACKER_ID = 1;
	
	private final static long POINT_ID = 2L;

	
	private final static String GGA_LATITUDE = "4025.7313N"; 
	private final static String GGA_LONGITUDE = "00338.5613W";
	private final static String GGA_LATITUDE_WRONG_FORMAT = "N4025.7313";

	private final static double ACCURACY = 2.0;
	private final static double ALTITUDE = 65.0;

	
	private final static Calendar TO = Calendar.getInstance();
	private final static Calendar FROM = Calendar.getInstance();
	private final static Calendar INVALID_FROM = Calendar.getInstance();
	static {
		FROM.add(Calendar.DAY_OF_MONTH, -100);
		INVALID_FROM.add(Calendar.DAY_OF_MONTH, 100);
	}
	
	private final static String ORDER_DESC = "DESC";
	private final static int PAGE = 1;
	private final static int NUM_REGS_PER_PAGE = 10;
	
	private final static RouteDTO ROUTE_DTO = RouteDTO.builder().routeId(ROUTE_ID).build();
	private final static PointDTO POINT_DTO = forgePointDTO(POINT_ID);
	private final static List<RouteDTO> LIST_ROUTE_DTO = singletonList(ROUTE_DTO);
	private final static List<PointDTO> LIST_POINT_DTO = singletonList(POINT_DTO);
	
	@Mock
	private TrackService trackService;
	
	@Mock
	private RouteMgr routeMgr;
	
	@Mock
	private PointMgr pointMgr;
	
	@Mock
	private AccessControl accessControl;
	
	@Mock
	private TrackValidator trackValidator;

	@InjectMocks
	private TrackV1Ctrl trackV1Ctrl;
	
	/*
				           _     _ ____       _       _ __     ___ 
				  __ _  __| | __| |  _ \ ___ (_)_ __ | |\ \   / / |
				 / _` |/ _` |/ _` | |_) / _ \| | '_ \| __\ \ / /| |
				| (_| | (_| | (_| |  __/ (_) | | | | | |_ \ V / | |
				 \__,_|\__,_|\__,_|_|   \___/|_|_| |_|\__| \_/  |_|
                                                   
	 */

	@Test
	public void givenGeoLocationParamenters_whenAddPoint_thenPointIsAdded() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		
		trackV1Ctrl.addPointV1(GGA_LATITUDE, GGA_LONGITUDE, ACCURACY, ALTITUDE);
		
		verify(accessControl).getUserIdFromSecurityContext();
		verify(trackService).addPoint(forgePointDTO(null), USER_ID);
	}
	
	@Test(expected = ParserTrackerException.class)
	public void givenWrongFormatLatitude_whenAddPoint_thenParserTrackerExceptionIsThrown() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);

		trackV1Ctrl.addPointV1(GGA_LATITUDE_WRONG_FORMAT, GGA_LONGITUDE, ACCURACY, ALTITUDE);
	}

	
	private static PointDTO forgePointDTO(Long pointId) {
		return PointDTO.builder()
				.pointId(pointId)
				.latitude(TrackerUtils.convertToDecimalDegrees(GGA_LATITUDE))
				.longitude(TrackerUtils.convertToDecimalDegrees(GGA_LONGITUDE))
				.accuracy(ACCURACY)
				.altitude(ALTITUDE)
				.build();
	}
	
	/*
				            _   _     _     _    ___   __ ____             _          __     ___ 
				  __ _  ___| |_| |   (_)___| |_ / _ \ / _|  _ \ ___  _   _| |_ ___  __\ \   / / |
				 / _` |/ _ \ __| |   | / __| __| | | | |_| |_) / _ \| | | | __/ _ \/ __\ \ / /| |
				| (_| |  __/ |_| |___| \__ \ |_| |_| |  _|  _ < (_) | |_| | ||  __/\__ \\ V / | |
				 \__, |\___|\__|_____|_|___/\__|\___/|_| |_| \_\___/ \__,_|\__\___||___/ \_/  |_|
				 |___/                                                                           
	*/
	
	@Test
	public void shouldReturnListOfRoutes() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		PageRequest pageRequest = getPageRequest(PAGE, NUM_REGS_PER_PAGE, ORDER_DESC);
		when(routeMgr.getRoutes(USER_ID, empty(), FROM.toInstant(), TO.toInstant(),pageRequest)).thenReturn(LIST_ROUTE_DTO);
		
		trackV1Ctrl.getListOfRoutesV1(null, FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	
		verify(accessControl).getUserIdFromSecurityContext();
		verify(routeMgr).getRoutes(USER_ID, empty(), FROM.toInstant(), TO.toInstant(), pageRequest);
	}
	
	@Test(expected = ParserTrackerException.class)
	public void listOfRoutesShouldThrowParserTrackerException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		doThrow(new ParserTrackerException(1, "", new Exception())).when(trackValidator).validateGetTracks(INVALID_FROM.toInstant(), TO.toInstant(), ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
		
		trackV1Ctrl.getListOfRoutesV1(null, INVALID_FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void listOfRoutesShouldThrowFelineNoContentException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		PageRequest pageRequest = getPageRequest(PAGE, NUM_REGS_PER_PAGE, ORDER_DESC);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(routeMgr).getRoutes(USER_ID, empty(), FROM.toInstant(), TO.toInstant(), pageRequest);
		
		trackV1Ctrl.getListOfRoutesV1(null, FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	}
	
	@Test
	public void shouldReturnListOfRoutesWhenNonNullTrackerId() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		PageRequest pageRequest = getPageRequest(PAGE, NUM_REGS_PER_PAGE, ORDER_DESC);
		when(routeMgr.getRoutes(USER_ID, of(TRACKER_ID), FROM.toInstant(), TO.toInstant(),pageRequest)).thenReturn(LIST_ROUTE_DTO);
		
		trackV1Ctrl.getListOfRoutesV1(TRACKER_ID, FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	
		verify(accessControl).getUserIdFromSecurityContext();
		verify(routeMgr).getRoutes(USER_ID, of(TRACKER_ID), FROM.toInstant(), TO.toInstant(), pageRequest);
	}
	
	@Test(expected = ParserTrackerException.class)
	public void shouldThrowParserTrackerExceptionWhenNonNullTrackerId() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		doThrow(new ParserTrackerException(1, "", new Exception())).when(trackValidator).validateGetTracks(INVALID_FROM.toInstant(), TO.toInstant(), ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
		
		trackV1Ctrl.getListOfRoutesV1(TRACKER_ID, INVALID_FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void shouldThrowFelineNoContentExceptionWhenNonNullTrackerId() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		PageRequest pageRequest = getPageRequest(PAGE, NUM_REGS_PER_PAGE, ORDER_DESC);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(routeMgr).getRoutes(USER_ID, of(TRACKER_ID), FROM.toInstant(), TO.toInstant(), pageRequest);
		
		trackV1Ctrl.getListOfRoutesV1(TRACKER_ID, FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	}
	
	private PageRequest getPageRequest(int page, int numRegistersPerPage, String orderAscDesc) {
		return new PageRequest(page, numRegistersPerPage, Sort.Direction.valueOf(orderAscDesc), TrackV1Ctrl.ORDER_BY_START_DATE);
	}
	
	/*
				            _   _     _     _    ___   __ ____       _       _       ____        ____             _     __     ___ 
				  __ _  ___| |_| |   (_)___| |_ / _ \ / _|  _ \ ___ (_)_ __ | |_ ___| __ ) _   _|  _ \ ___  _   _| |_ __\ \   / / |
				 / _` |/ _ \ __| |   | / __| __| | | | |_| |_) / _ \| | '_ \| __/ __|  _ \| | | | |_) / _ \| | | | __/ _ \ \ / /| |
				| (_| |  __/ |_| |___| \__ \ |_| |_| |  _|  __/ (_) | | | | | |_\__ \ |_) | |_| |  _ < (_) | |_| | ||  __/\ V / | |
				 \__, |\___|\__|_____|_|___/\__|\___/|_| |_|   \___/|_|_| |_|\__|___/____/ \__, |_| \_\___/ \__,_|\__\___| \_/  |_|
				 |___/                                                                     |___/                                   

	 */
	
	@Test
	public void shouldReturnListOfPoints() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(pointMgr.getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID)).thenReturn(LIST_POINT_DTO);
		
		List<PointDTO> points = trackV1Ctrl.getListOfPointsByRouteV1(ROUTE_ID);
		
		assertTrue(points.size() == 1);
		assertEquals(Long.valueOf(POINT_ID), points.get(0).getPointId()); 
		verify(accessControl).getUserIdFromSecurityContext();
		verify(pointMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void shouldThrowNoContentException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(pointMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
		
		trackV1Ctrl.getListOfPointsByRouteV1(ROUTE_ID);
	}
	
	/*
				            _    ____           _             _ ____       _       _   
				  __ _  ___| |_ / ___|___ _ __ | |_ _ __ __ _| |  _ \ ___ (_)_ __ | |_ 
				 / _` |/ _ \ __| |   / _ \ '_ \| __| '__/ _` | | |_) / _ \| | '_ \| __|
				| (_| |  __/ |_| |__|  __/ | | | |_| | | (_| | |  __/ (_) | | | | | |_ 
				 \__, |\___|\__|\____\___|_| |_|\__|_|  \__,_|_|_|   \___/|_|_| |_|\__|
				 |___/                                                                 

	*/

	@Test
	public void shouldReturnCentralPoint() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(pointMgr.getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID)).thenReturn(LIST_POINT_DTO);
		
		trackV1Ctrl.getCentralPointV1(ROUTE_ID);
		
		verify(accessControl).getUserIdFromSecurityContext();
		verify(pointMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void getCentralPointShouldThrowFelineNoContentException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(pointMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
		
		trackV1Ctrl.getCentralPointV1(ROUTE_ID);
	}
	
	/*
				            _   _              _   ____             _       
				  __ _  ___| |_| |    __ _ ___| |_|  _ \ ___  _   _| |_ ___ 
				 / _` |/ _ \ __| |   / _` / __| __| |_) / _ \| | | | __/ _ \
				| (_| |  __/ |_| |__| (_| \__ \ |_|  _ < (_) | |_| | ||  __/
				 \__, |\___|\__|_____\__,_|___/\__|_| \_\___/ \__,_|\__\___|
				 |___/                                                      

	 */
	
	@Test
	public void givenAppUser_whenGetLastRoute_thenReturnsLastRoute() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(routeMgr.getLastRoute(USER_ID, empty())).thenReturn(of(ROUTE_DTO));
		
		RouteDTO routeDTO = trackV1Ctrl.getLastRouteV1(null);
		
		assertTrue(routeDTO != null);
		assertEquals(ROUTE_DTO, routeDTO);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void givenNoRouteForAppUser_whenGetLastRoute_thenThrowsException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(routeMgr.getLastRoute(USER_ID, empty())).thenReturn(empty());
		
		trackV1Ctrl.getLastRouteV1(null);
	}
	
	@Test
	public void givenAppUserAndTraUser_whenGetLastRoute_thenReturnsLastRoute() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(routeMgr.getLastRoute(USER_ID, of(TRACKER_ID))).thenReturn(of(ROUTE_DTO));
		
		RouteDTO routeDTO = trackV1Ctrl.getLastRouteV1(TRACKER_ID);
		
		assertTrue(routeDTO != null);
		assertEquals(ROUTE_DTO, routeDTO);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void givenNoRouteForAppUserAndTraUser_whenGetLastRoute_thenThrowsException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(USER_ID);
		when(routeMgr.getLastRoute(USER_ID, of(TRACKER_ID))).thenReturn(empty());
		
		trackV1Ctrl.getLastRouteV1(TRACKER_ID);
	}
}
