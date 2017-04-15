package com.ayoza.feline.app.web.rest.v1.tracker;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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

import ayoza.com.feline.api.entities.common.dto.UserDTO;
import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.exceptions.FelineNoContentException;
import ayoza.com.feline.api.exceptions.UserServicesException;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;

@RunWith(MockitoJUnitRunner.class)
public class TrackV1CtrlTest {
	
	private final static int USER_ID = 1; 
	private final static int ROUTE_ID = 1;
	
	private final static long LAST_POINT_ID = 1L;
	private final static long POINT_ID = 2L;
	private final static double LAST_LATITUDE_FARAWAY = 27.0;
	private final static double LAST_LONGITUDE_FARAWAY = -3.0;
	
	private final static String GGA_LATITUDE = "4025.7313N"; 
	private final static String GGA_LONGITUDE = "00338.5613W";
	private final static double LATITUDE = 40.428855;
	private final static double LONGITUDE = -3.6426883333333335;
	private final static double ACCURACY = 2.0;
	private final static double ALTITUDE = 65.0;
	private final static String GGA_LATITUDE_WRONG_FORMAT = "N4025.7313";
	
	private final static double LAST_CLOSE_LATITUDE = LATITUDE;
	private final static double LAST_CLOSE_LONGITUDE = LONGITUDE;
	
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
	
	private final UserDTO userDTO = forgeUserDTO();
	private final RouteDTO routeDTO = RouteDTO.builder().routeId(ROUTE_ID).build();
	private final PointDTO lastFarawayPointDTO = forgeLastFarawayPointDTO();
	private final PointDTO lastClosePointDTO = forgeLastClosePointDTO();
	private final PointDTO pointDTO = PointDTO.builder().pointId(POINT_ID).build();
	private final List<RouteDTO> listRouteDTO = singletonList(routeDTO);
	private final List<PointDTO> listPointDTO = singletonList(pointDTO);
	
	@Mock
	private TrackerMgr trackerMgr;
	
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
	public void existingLastPoint_ShouldNotCreateNewRoute_ShouldNotUpdateLastPoint_ShouldCreateNewPoint() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getCurrentRoute(eq(USER_ID), any())).thenReturn(of(routeDTO));
		when(trackerMgr.getLastPoint(ROUTE_ID)).thenReturn(of(lastFarawayPointDTO));
		
		trackV1Ctrl.addPointV1(GGA_LATITUDE, GGA_LONGITUDE, ACCURACY, ALTITUDE);
		
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).createRoute(anyInt());
		verify(trackerMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr).addPointToRoute(any(), any());
	}
	
	@Test
	public void existingLastPoint_ShouldNotCreateNewRoute_ShouldUpdateLastPoint_ShouldNotCreateNewPoint() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getCurrentRoute(eq(USER_ID), any())).thenReturn(of(routeDTO));
		when(trackerMgr.getLastPoint(ROUTE_ID)).thenReturn(of(lastClosePointDTO));
		when(trackerMgr.updatePoint(anyLong(), any())).thenReturn(lastClosePointDTO);
		
		trackV1Ctrl.addPointV1(GGA_LATITUDE, GGA_LONGITUDE, ACCURACY, ALTITUDE);

		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).createRoute(anyInt());
		verify(trackerMgr).updatePoint(anyLong(), any());
		verify(trackerMgr, never()).addPointToRoute(any(), any());
	}
	
	@Test
	public void nonExistingLastPoint_ShouldNotCreateNewRoute_ShouldNotUpdateLastPoint_ShouldCreateNewPoint() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getCurrentRoute(eq(USER_ID), any())).thenReturn(of(routeDTO));
		when(trackerMgr.getLastPoint(ROUTE_ID)).thenReturn(empty());
		
		trackV1Ctrl.addPointV1(GGA_LATITUDE, GGA_LONGITUDE, ACCURACY, ALTITUDE);

		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).createRoute(anyInt());
		verify(trackerMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr).addPointToRoute(any(), any());
	}
	
	@Test
	public void shouldCreateNewRoute_ShouldNotUpdateLastPoint_ShouldCreateNewPoint() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getCurrentRoute(anyInt(), any())).thenReturn(empty());
		when(trackerMgr.createRoute(anyInt())).thenReturn(routeDTO);
		when(trackerMgr.getLastPoint(ROUTE_ID)).thenReturn(empty());
		
		trackV1Ctrl.addPointV1(GGA_LATITUDE, GGA_LONGITUDE, ACCURACY, ALTITUDE);
		
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).createRoute(anyInt());
		verify(trackerMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr).addPointToRoute(any(), any());
	}
	
	@Test(expected = UserServicesException.class)
	public void addPointShouldThrowUserServicesException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(empty());

		trackV1Ctrl.addPointV1(GGA_LATITUDE, GGA_LONGITUDE, ACCURACY, ALTITUDE);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).createRoute(anyInt());
		verify(trackerMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr, never()).addPointToRoute(any(), any());
	}
	
	@Test(expected = ParserTrackerException.class)
	public void addPointShouldThrowParserTrackerException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getCurrentRoute(eq(USER_ID), any())).thenReturn(of(routeDTO));

		trackV1Ctrl.addPointV1(GGA_LATITUDE_WRONG_FORMAT, GGA_LONGITUDE, ACCURACY, ALTITUDE);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).createRoute(anyInt());
		verify(trackerMgr, never()).updatePoint(anyLong(), any());
		verify(trackerMgr, never()).addPointToRoute(any(), any());
	}

	private UserDTO forgeUserDTO() {
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(USER_ID);
		return userDTO;
	}
	
	private PointDTO forgeLastFarawayPointDTO() {
		return PointDTO.builder()
				.pointId(LAST_POINT_ID)
				.latitude(LAST_LATITUDE_FARAWAY)
				.longitude(LAST_LONGITUDE_FARAWAY)
				.build();
	}
	
	private PointDTO forgeLastClosePointDTO() {
		return PointDTO.builder()
				.pointId(LAST_POINT_ID)
				.latitude(LAST_CLOSE_LATITUDE)
				.longitude(LAST_CLOSE_LONGITUDE)
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
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		PageRequest pageRequest = getPageRequest(PAGE, NUM_REGS_PER_PAGE, ORDER_DESC);
		when(trackerMgr.getRouteByAppUserAndFromStartDate(USER_ID, FROM.toInstant(), TO.toInstant(),pageRequest)).thenReturn(listRouteDTO);
		
		trackV1Ctrl.getListOfRoutesV1(FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).getRouteByAppUserAndFromStartDate(USER_ID, FROM.toInstant(), TO.toInstant(), pageRequest);
	}
	
	@Test(expected = UserServicesException.class)
	public void listOfRoutesShouldThrowUserServicesException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(empty());

		trackV1Ctrl.getListOfRoutesV1(INVALID_FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	
		verify(accessControl, never()).getUserFromSecurityContext();
		verify(trackerMgr, never()).getRouteByAppUserAndFromStartDate(any(), any(), any(), any());
	}
	
	@Test(expected = ParserTrackerException.class)
	public void listOfRoutesShouldThrowParserTrackerException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		doThrow(new ParserTrackerException(1, "", new Exception())).when(trackValidator).validateGetTracks(INVALID_FROM.toInstant(), TO.toInstant(), ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
		
		trackV1Ctrl.getListOfRoutesV1(INVALID_FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).getRouteByAppUserAndFromStartDate(any(), any(), any(), any());
	}
	
	@Test(expected = FelineNoContentException.class)
	public void listOfRoutesShouldThrowFelineNoContentException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		PageRequest pageRequest = getPageRequest(PAGE, NUM_REGS_PER_PAGE, ORDER_DESC);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(trackerMgr).getRouteByAppUserAndFromStartDate(USER_ID, FROM.toInstant(), TO.toInstant(), pageRequest);
		
		trackV1Ctrl.getListOfRoutesV1(FROM, TO, ORDER_DESC, PAGE, NUM_REGS_PER_PAGE);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).getRouteByAppUserAndFromStartDate(any(), any(), any(), any());
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
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID)).thenReturn(listPointDTO);
		
		trackV1Ctrl.getListOfPointsByRouteV1(ROUTE_ID);
		
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void shouldThrowNoContentException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(trackerMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
		
		trackV1Ctrl.getListOfPointsByRouteV1(ROUTE_ID);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
	
	@Test(expected = UserServicesException.class)
	public void shouldThrowUserServicesException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(empty());

		trackV1Ctrl.getListOfPointsByRouteV1(ROUTE_ID);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
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
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		when(trackerMgr.getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID)).thenReturn(listPointDTO);
		
		trackV1Ctrl.getCentralPointV1(ROUTE_ID);
		
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
	
	@Test(expected = UserServicesException.class)
	public void getCentralPointShouldThrowUserServicesException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(empty());

		trackV1Ctrl.getCentralPointV1(ROUTE_ID);
	
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr, never()).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void getCentralPointShouldThrowFelineNoContentException() {
		when(accessControl.getUserFromSecurityContext()).thenReturn(of(userDTO));
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(trackerMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
		
		trackV1Ctrl.getCentralPointV1(ROUTE_ID);
		
		verify(accessControl).getUserFromSecurityContext();
		verify(trackerMgr).getPointsByTraRouteIdAndAppUserId(ROUTE_ID, USER_ID);
	}
}
