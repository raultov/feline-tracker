package com.ayoza.feline.app.web.rest.v1.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping(value = "/v1/cache")
@AllArgsConstructor(onConstructor=@__({@Autowired}))
@FieldDefaults(level=AccessLevel.PRIVATE)
public class CacheV1Ctrl {
	
	public static final String POINTS_CACHE = "points";

	// TODO create evict methods and grant access only for administrator users (OAUTH2 configuration)
}
