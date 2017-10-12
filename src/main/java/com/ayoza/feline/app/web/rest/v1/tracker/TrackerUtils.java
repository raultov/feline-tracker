package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT;
import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT_MSG;
import static java.util.Optional.ofNullable;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.exceptions.FelineNoContentException;

final class TrackerUtils {
	
	private static final double MINUTES = 60.0;
	private static final double SIGN_N_E = 1.0;
	private static final double SIGN_S_W = -1.0;
	private static final int PLACES_N_S = 2;
	private static final int PLACES_E_W = 3;

	private TrackerUtils() {
		throw new UnsupportedOperationException("Default constructor cannot be invoked");
	}

	static Double convertToDecimalDegrees(String gga) throws ParserTrackerException {
		Double degrees, minutes, sign;
		char cardinalPoint;
		int places;
		
		try {
			cardinalPoint = gga.charAt(gga.length()-1);		
			switch (cardinalPoint) {
				case 'N':
					sign = SIGN_N_E;
					places = PLACES_N_S;
					break;
				case 'E':
					sign = SIGN_N_E;
					places = PLACES_E_W;
					break;
					
				case 'S':
					sign = SIGN_S_W;
					places = PLACES_N_S;
					break;
				case 'W':
					sign = SIGN_S_W;
					places = PLACES_E_W;
					break;
					
				default:
					throw new ParserTrackerException(WRONG_GPGGA_FORMAT, WRONG_GPGGA_FORMAT_MSG, new Exception(WRONG_GPGGA_FORMAT_MSG));
			}
			
			degrees = new Double(gga.substring(0, places));
			minutes = new Double(gga.substring(places, gga.length()-1));
			minutes /= MINUTES;
		} catch(NumberFormatException | IndexOutOfBoundsException | NullPointerException nfe) {
			throw new ParserTrackerException(WRONG_GPGGA_FORMAT, WRONG_GPGGA_FORMAT_MSG, nfe);
		}
		
		return (degrees + minutes) * sign;
	}
	

	static PointDTO getCentralApiTraPoint(List<PointDTO> geoCoordinates) {
		return ofNullable(geoCoordinates)
			.filter(CollectionUtils::isNotEmpty)
			.map(list -> {
				double minLat = Double.MAX_VALUE;
				double minLong = Double.MAX_VALUE;
				double maxLat = Double.MIN_VALUE;
				double maxLong = Double.MIN_VALUE;
				
				for (PointDTO point : list) {
					minLat = point.getLatitude() < minLat ? point.getLatitude() : minLat;
					minLong = point.getLongitude() < minLong ? point.getLongitude() : minLong;
					maxLat = point.getLatitude() > minLat ? point.getLatitude() : maxLat;
					maxLong = point.getLongitude() > minLat ? point.getLongitude() : maxLong;
				}
				
				return PointDTO.builder()
						.latitude(minLat + ((maxLat-minLat) / 2.0))
						.longitude(minLong + ((maxLong-minLong) / 2.0))
						.build();
			})
			.orElseThrow(() -> FelineNoContentException.Exceptions.NO_CONTENT.getException());
    }	
}
