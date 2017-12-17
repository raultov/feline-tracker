package com.ayoza.feline.app.web.rest.v1.tracker;

import static java.lang.Math.abs;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.entities.tracker.dto.RouteDTO;
import ayoza.com.feline.api.managers.tracker.PointMgr;
import ayoza.com.feline.api.managers.tracker.RouteMgr;
import ayoza.com.feline.api.managers.tracker.TrackerMgr;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrackService {
	
	private static final double MIN_DIFF = 0.0002;
	
	private final RouteMgr routeMgr;
	
	private final PointMgr pointMgr;
	
	private final TrackerMgr trackerMgr;
	
	@Async
	public Future<PointDTO> addPoint(final PointDTO pointDTO, int userId) {
		Instant from = Instant.now().minus(2, MINUTES);
		
		RouteDTO routeDTO = routeMgr.getLastRouteFrom(userId, from)
							.orElseGet((() -> routeMgr.createRoute(userId)));
		
		Optional<PointDTO> lastPoint = pointMgr.getLastPoint(routeDTO.getRouteId());

		Optional<PointDTO> updatedPoint = lastPoint
				.filter(t -> abs(t.getLatitude() - pointDTO.getLatitude()) < MIN_DIFF)
				.filter(t -> abs(t.getLongitude() - pointDTO.getLongitude()) < MIN_DIFF)
				.map(t -> pointMgr.updatePoint(t.getPointId(), pointDTO.withWhen(now())));

		return new AsyncResult<PointDTO>(updatedPoint.orElseGet(() -> trackerMgr.addPointToRoute(pointDTO, routeDTO)));
	}
}
