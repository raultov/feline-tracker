package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT;
import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT_MSG;

import java.util.List;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;

class TrackerUtils {
	
	private static final double MINUTES = 60.0;
	private static final double SIGN_N_E = 1.0;
	private static final double SIGN_S_W = -1.0;
	private static final int PLACES_N_S = 2;
	private static final int PLACES_E_W = 3;

	private TrackerUtils() {
	}

	public static Double convertToDecimalDegrees(String gga) throws ParserTrackerException {
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
		
		if (geoCoordinates == null || geoCoordinates.size() == 0) {
            return null;
        }
		
        if (geoCoordinates.size() == 1) {
            return geoCoordinates.get(0);
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (PointDTO apiTraPoint : geoCoordinates) {
            double latitude = apiTraPoint.getLatitude() * Math.PI / 180;
            double longitude = apiTraPoint.getLongitude() * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        int total = geoCoordinates.size();

        x = x / total;
        y = y / total;
        z = z / total;

        double centralLongitude = Math.atan2(y, x);
        double centralSquareRoot = Math.sqrt(x * x + y * y);
        double centralLatitude = Math.atan2(z, centralSquareRoot);
        
        PointDTO result = new PointDTO();
        result.setLatitude(centralLatitude * 180 / Math.PI);
        result.setLongitude(centralLongitude * 180 / Math.PI);

        return result;
    }	
}
