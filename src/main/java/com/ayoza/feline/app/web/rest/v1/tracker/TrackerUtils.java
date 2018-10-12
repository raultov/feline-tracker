package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT;
import static com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException.WRONG_GPGGA_FORMAT_MSG;
import static java.lang.Double.parseDouble;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

final class TrackerUtils {

	private TrackerUtils() {
		throw new UnsupportedOperationException("Default constructor should not be invoked");
	}

	static Double convertToDecimalDegrees(String gga) throws ParserTrackerException {
		double sign;
		double decimal;
		char cardinalPoint;

		try {
			cardinalPoint = gga.charAt(gga.length()-1);	
			if (cardinalPoint == 'N' || cardinalPoint == 'E') {
			    sign = 1.0;
			} else if (cardinalPoint == 'S' || cardinalPoint == 'W') {
			    sign = -1.0;
			} else {
			    throw new ParserTrackerException(WRONG_GPGGA_FORMAT, WRONG_GPGGA_FORMAT_MSG, new Exception(WRONG_GPGGA_FORMAT_MSG));   
			}
			
			decimal = toDecimal(parseDouble(gga.substring(0, gga.length()-1)));
			
		} catch(Exception e) {
			throw new ParserTrackerException(WRONG_GPGGA_FORMAT, WRONG_GPGGA_FORMAT_MSG, e);
		}
		
		return decimal * sign;
	}
	
    private static double toDecimal(double d) {
        BigDecimal degrees = getDegrees(d);
        BigDecimal minutesAndSeconds = getMinutes(d);
        BigDecimal decimal = degrees.add(minutesAndSeconds).setScale(4, RoundingMode.HALF_EVEN);

        return decimal.doubleValue();
    }

    private static BigDecimal getDegrees(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.movePointLeft(2);

        return new BigDecimal(bd.intValue());
    }

    private static BigDecimal getMinutes(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.movePointLeft(2);

        BigDecimal minutesBd = bd.subtract(new BigDecimal(bd.intValue()));
        minutesBd = minutesBd.movePointRight(2);

        BigDecimal minutes = new BigDecimal((minutesBd.doubleValue() * 100) / 60).movePointLeft(2);

        return minutes;
    }
}
