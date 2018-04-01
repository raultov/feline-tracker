package com.ayoza.feline.app.web.rest.v1.tracker;

import static java.lang.Math.abs;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class TrackService {
	
	private static final double MIN_DIFF = 0.0002;
	
	private final RouteMgr routeMgr;
	
	private final PointMgr pointMgr;
	
	private final TrackerMgr trackerMgr;
	
	@Value("${threshold.create.new.route.in.minutes}")
	private int thresholdCreateNewRoute;
	
	@Async
	public void addPoint(final PointDTO pointDTO, String simPhone) {
		Instant now = now();
		Instant from = now.minus(thresholdCreateNewRoute, MINUTES);
		
		RouteDTO routeDTO = routeMgr.getLastRouteFrom(simPhone, from)
							.orElseGet((() -> routeMgr.createRoute(simPhone)));
		
		Optional<PointDTO> lastPoint = pointMgr.getLastPoint(routeDTO.getTrackId());

		Optional<PointDTO> updatedPoint = lastPoint
				.filter(t -> abs(t.getLatitude() - pointDTO.getLatitude()) < MIN_DIFF)
				.filter(t -> abs(t.getLongitude() - pointDTO.getLongitude()) < MIN_DIFF)
				.map(t -> pointMgr.updatePoint(t.getPointId(), pointDTO.withWhen(now)));

		updatedPoint.orElseGet(() -> trackerMgr.addPointToRoute(pointDTO, routeDTO, now));
		
		routeMgr.updateRoute(routeDTO.getTrackId(), routeDTO.withEndDate(now));
	}
}
