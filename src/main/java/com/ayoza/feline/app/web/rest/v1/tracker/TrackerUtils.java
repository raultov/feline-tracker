package com.ayoza.feline.app.web.rest.v1.tracker;

import java.util.List;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;

final class TrackerUtils {

	private TrackerUtils() {
	}
	
	public static Double extractLatitude(String ggaLatitude) throws ParserTrackerException {
		
		Double degrees = 0.0, minutes = 0.0;
		
		try {
			degrees = new Double(ggaLatitude.substring(0, 2));
			minutes = new Double(ggaLatitude.substring(2, ggaLatitude.length()-1));
		} catch(NumberFormatException nfe) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_GPGGA_FORMAT,
												ParserTrackerException.WRONG_GPGGA_FORMAT_MSG,
												nfe);
		}
		
		double sign = ggaLatitude.charAt(ggaLatitude.length()-1) == 'N' ? 1.0 : -1.0;
		
		minutes = minutes / 60.0;
		
		return (degrees + minutes) * sign;
	}
	
	public static Double extractLongitude(String ggaLongitude) throws ParserTrackerException {
		
		Double degrees = 0.0, minutes = 0.0;
		
		try {
			degrees = new Double(ggaLongitude.substring(0, 3));
			minutes = new Double(ggaLongitude.substring(3, ggaLongitude.length()-1));
		} catch(NumberFormatException | IndexOutOfBoundsException nfe) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_GPGGA_FORMAT,
												ParserTrackerException.WRONG_GPGGA_FORMAT_MSG,
												nfe);
		}
		
		double sign = ggaLongitude.charAt(ggaLongitude.length()-1) == 'E' ? 1.0 : -1.0;
		
		minutes = minutes / 60.0;
		
		return (degrees + minutes) * sign;
	}	
	

	public static PointDTO getCentralApiTraPoint(List<PointDTO> geoCoordinates) {
		
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
