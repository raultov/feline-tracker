package com.ayoza.feline.app.web.rest.v1.cache;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ayoza.com.feline.api.audit.Auditable;
import ayoza.com.feline.api.managers.CacheMgr;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/cache")
@RequiredArgsConstructor
public class CacheV1Ctrl {
	
	private final CacheMgr cacheMgr;

	@Auditable
	@RequestMapping(value = "/all", method = DELETE)
    public ResponseEntity<Void> clearAll() {
		cacheMgr.clearAll();
		return ResponseEntity.noContent().build();
	}

	@Auditable
	@RequestMapping(value = "/tracks/{trackId}", method = DELETE)
	public ResponseEntity<Void> clearPointsByTrack(@PathVariable(value="trackId") UUID trackId) {
		cacheMgr.clearPointsByTrack(trackId);
		return ResponseEntity.noContent().build();
	}

	@Auditable
	@RequestMapping(value = "/users/{appUserId}", method = DELETE)
	public ResponseEntity<Void> clearAppUserById(Integer appUserId) {
		cacheMgr.clearAppUserById(appUserId);
		return ResponseEntity.noContent().build();
	}

	@Auditable
	@RequestMapping(value = "/trackers/{traUserId}", method = DELETE)
	public ResponseEntity<Void> clearTraUserById(Integer traUserId) {
		cacheMgr.clearTraUserById(traUserId);
		return ResponseEntity.noContent().build();
	}
	
	@Auditable
	@RequestMapping(value = "/users/{username}", method = DELETE)
	public ResponseEntity<Void> clearUsersByUsername(String username) {
		cacheMgr.clearUsersByUsername(username);
		return ResponseEntity.noContent().build();
	}
	
	@Auditable
	@RequestMapping(value = "/userDetails/{username}", method = DELETE)
	public ResponseEntity<Void> clearUsersDetailsByUsername(String username) {
		cacheMgr.clearUsersDetailsByUsername(username);
		return ResponseEntity.noContent().build();
	}
}
