package com.ayoza.feline.app.web.rest.v1.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ayoza.com.feline.api.managers.CacheMgr;

@RunWith(MockitoJUnitRunner.class)
public class CacheV1CtrlTest {
	
	@Mock
	private CacheMgr cacheMgr;

	@InjectMocks
	private CacheV1Ctrl cacheV1Ctrl;

	@Test
	public void shouldAcceptCacheMgrAsCollaborator() {
		new CacheV1Ctrl(cacheMgr);
	}

	@Test
	public void shouldDelegateToReturnNoContentWhenRemovingAllCaches() {
		assertThat(cacheV1Ctrl.clearAll().getStatusCode()).isEqualTo(NO_CONTENT);
		
		verify(cacheMgr).clearAll();
	}
	
	@Test
	public void shouldDelegateToReturnNoContentWhenRemovingPointsCacheGivenATrackId() {
		assertThat(cacheV1Ctrl.clearPointsByTrack(1).getStatusCode()).isEqualTo(NO_CONTENT);
		
		verify(cacheMgr).clearPointsByTrack(anyInt());
	}
	
	@Test
	public void shouldDelegateToReturnNoContentWhenRemovingAppUserCacheGivenAnAppUserId() {
		assertThat(cacheV1Ctrl.clearAppUserById(1).getStatusCode()).isEqualTo(NO_CONTENT);
		
		verify(cacheMgr).clearAppUserById(anyInt());
	}
	
	@Test
	public void shouldDelegateToReturnNoContentWhenRemovingTraUserCacheGivenATraUserId() {
		assertThat(cacheV1Ctrl.clearTraUserById(1).getStatusCode()).isEqualTo(NO_CONTENT);
		
		verify(cacheMgr).clearTraUserById(anyInt());
	}
	
	@Test
	public void shouldDelegateToReturnNoContentWhenRemovingUsersCacheGivenAnUsername() {
		assertThat(cacheV1Ctrl.clearUsersByUsername("username").getStatusCode()).isEqualTo(NO_CONTENT);
		
		verify(cacheMgr).clearUsersByUsername(anyString());
	}
	
	@Test
	public void shouldDelegateToReturnNoContentWhenRemovingUserDetailsCacheGivenAnUsername() {
		assertThat(cacheV1Ctrl.clearUsersDetailsByUsername("username").getStatusCode()).isEqualTo(NO_CONTENT);
		
		verify(cacheMgr).clearUsersDetailsByUsername(anyString());
	}
}
