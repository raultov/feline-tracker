package com.ayoza.feline.app.web.rest.v1.user;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import ayoza.com.feline.api.entities.tracker.dto.TraUserDTO;
import ayoza.com.feline.api.exceptions.FelineNoContentException;
import ayoza.com.feline.api.managers.TraUserMgr;

@RunWith(MockitoJUnitRunner.class)
public class TrackersV1CtrlTest {
	
	private final static int APP_USER_ID = 1;
	private final static TraUserDTO TRA_USER_DTO = mock(TraUserDTO.class);
	private final static List<TraUserDTO> TRA_USERS_DTO = singletonList(TRA_USER_DTO);
	
	@Mock
	private TraUserMgr traUserMgr;
	
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
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(APP_USER_ID);
		when(traUserMgr.getTraUsersByAppUserId(APP_USER_ID)).thenReturn(TRA_USERS_DTO);
		
		List<TraUserDTO> trackers = trackersV1Ctrl.getTrackers();
		
		assertTrue(trackers.size() == 1);
		assertEquals(TRA_USER_DTO, trackers.get(0)); 
		verify(accessControl).getUserIdFromSecurityContext();
		verify(traUserMgr).getTraUsersByAppUserId(APP_USER_ID);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void shouldThrowFelineNoContentExceptionWhenNoTrackerFound() {
		when(accessControl.getUserIdFromSecurityContext()).thenReturn(APP_USER_ID);
		doThrow(FelineNoContentException.Exceptions.NO_CONTENT.getException()).when(traUserMgr).getTraUsersByAppUserId(APP_USER_ID);
	
		trackersV1Ctrl.getTrackers();
	}
}
