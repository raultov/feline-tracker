package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT;
import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT_MSG;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

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
}
