package com.ayoza.feline.app.web.rest.v1.user;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ayoza.com.feline.api.exceptions.FelineNoContentException;
import ayoza.com.feline.api.managers.TrackerUserMgr;
import ayoza.com.feline.api.user.dto.TrackerUserDTO;

@RunWith(MockitoJUnitRunner.class)
public class TrackersV1CtrlTest {
	
	private final static String EMAIL = randomAlphanumeric(10);
	private final static TrackerUserDTO TRACKER_USER_DTO = TrackerUserDTO.builder().build();
	private final static List<TrackerUserDTO> TRACKER_USERS_DTO = singletonList(TRACKER_USER_DTO);
	
	@Mock
	private TrackerUserMgr trackerUserMgr;
	
	@Mock
	private AccessControl accessControl;
	
	@InjectMocks
	private TrackersV1Ctrl trackersV1Ctrl;

/*
	            _   _     _     _    ___   __ _____               _                 ____           _                _   _               
	  __ _  ___| |_| |   (_)___| |_ / _ \ / _|_   _| __ __ _  ___| | _____ _ __ ___| __ ) _   _   / \   _ __  _ __ | | | |___  ___ _ __ 
	 / _` |/ _ \ __| |   | / __| __| | | | |_  | || '__/ _` |/ __| |/ / _ \ '__/ __|  _ \| | | | / _ \ | '_ \| '_ \| | | / __|/ _ \ '__|
	| (_| |  __/ |_| |___| \__ \ |_| |_| |  _| | || | | (_| | (__|   <  __/ |  \__ \ |_) | |_| |/ ___ \| |_) | |_) | |_| \__ \  __/ |   
	 \__, |\___|\__|_____|_|___/\__|\___/|_|   |_||_|  \__,_|\___|_|\_\___|_|  |___/____/ \__, /_/   \_\ .__/| .__/ \___/|___/\___|_|   
	 |___/                                                                                |___/        |_|   |_|                        
*/
	@Test
	public void shouldReturnListOfTrackersWhenValidAppUserId() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(EMAIL);
		when(trackerUserMgr.getTrackerUsersByEmail(EMAIL)).thenReturn(TRACKER_USERS_DTO);
		
		List<TrackerUserDTO> trackers = trackersV1Ctrl.getTrackersV1();
		
		assertTrue(trackers.size() == 1);
		assertEquals(TRACKER_USER_DTO, trackers.get(0)); 
		verify(accessControl).getUserIdFromSecurityContext();
		verify(trackerUserMgr).getTrackerUsersByEmail(EMAIL);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void shouldThrowFelineNoContentExceptionWhenNoTrackerFound() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(EMAIL);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException())
			.when(trackerUserMgr).getTrackerUsersByEmail(EMAIL);
	
		trackersV1Ctrl.getTrackersV1();
	}
	
	/*
	            _   ____        __             _ _  _____               _             
	  __ _  ___| |_|  _ \  ___ / _| __ _ _   _| | ||_   _| __ __ _  ___| | _____ _ __ 
	 / _` |/ _ \ __| | | |/ _ \ |_ / _` | | | | | __|| || '__/ _` |/ __| |/ / _ \ '__|
	| (_| |  __/ |_| |_| |  __/  _| (_| | |_| | | |_ | || | | (_| | (__|   <  __/ |   
	 \__, |\___|\__|____/ \___|_|  \__,_|\__,_|_|\__||_||_|  \__,_|\___|_|\_\___|_|   
	 |___/                                                                            

	 */
	
	@Test
	public void givenAppUser_whenGetDefaultTracker_thenReturnsDefaultTracker() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(EMAIL);
		when(trackerUserMgr.getDefaultTrackerUserByEmail(EMAIL)).thenReturn(TRACKER_USER_DTO);
		
		TrackerUserDTO trackerUserDTO = trackersV1Ctrl.getDefaultTrackerV1();
		
		assertTrue(trackerUserDTO != null);
		assertEquals(TRACKER_USER_DTO, trackerUserDTO); 
	}
	
	@Test(expected = FelineNoContentException.class)
	public void givenAppUserAndNonExistingDefaultTracker_whenGetDefaultTracker_thenThrowsFelineNoContentException() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(EMAIL);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException())
			.when(trackerUserMgr).getDefaultTrackerUserByEmail(EMAIL);
	
		trackersV1Ctrl.getDefaultTrackerV1();
	}
}
