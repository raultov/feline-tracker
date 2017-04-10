package com.ayoza.feline.app.web.rest.v1.tracker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TrackerUtilsTest {
	
	private final static String VALID_GGA_LATITUDE_1 = "4025.7313N"; 
	private final static double GGA_LATITUDE_DOUBLE_1 = 40.428855;
	private final static String VALID_GGA_LATITUDE_2 = "4025.7313S"; 
	private final static double GGA_LATITUDE_DOUBLE_2 = -40.428855;	
	
	private final static String INVALID_GGA_LATITUDE_1 = "A4025.7313N";
	private final static String INVALID_GGA_LATITUDE_2 = "4025.C7313S";
	
	private final static String VALID_GGA_LONGITUDE_1 = "00338.5613W";
	private final static double VALID_LONGITUDE_DOUBLE_1 = -3.6426883333333335;
	private final static String VALID_GGA_LONGITUDE_2 = "00338.5613E";
	private final static double VALID_LONGITUDE_DOUBLE_2 = 3.6426883333333335;
	
	private final static String INVALID_GGA_LONGITUDE_1 = "A00338.5613W";
	private final static String INVALID_GGA_LONGITUDE_2 = "00338.T5613E";

	private final static String INVALID_GGA_1 = "4025.7313R";
	private final static String INVALID_GGA_2 = "00338.5613";
	private final static String INVALID_GGA_3 = "AAAAAAAA";

	@Test
	@Parameters({ VALID_GGA_LATITUDE_1 + "," + GGA_LATITUDE_DOUBLE_1, 
				  VALID_GGA_LATITUDE_2 + "," + GGA_LATITUDE_DOUBLE_2 })
	public void validGGALatitude(String gga, double expected) {
		assertThat(TrackerUtils.convertToDecimalDegrees(gga)).isEqualTo(expected);
	}
	
	@Test(expected = ParserTrackerException.class)
	@Parameters({ INVALID_GGA_LATITUDE_1, 
				  INVALID_GGA_LATITUDE_2 })
	public void invalidGGALatitude(String invalidGGA) {
		TrackerUtils.convertToDecimalDegrees(invalidGGA);
	}
	
	@Test
	@Parameters({ VALID_GGA_LONGITUDE_1 + "," + VALID_LONGITUDE_DOUBLE_1, 
				  VALID_GGA_LONGITUDE_2 + "," + VALID_LONGITUDE_DOUBLE_2 })
	public void validGGALongitude(String gga, double expected) {
		assertThat(TrackerUtils.convertToDecimalDegrees(gga)).isEqualTo(expected);
	}
	
	@Test(expected = ParserTrackerException.class)
	@Parameters({ INVALID_GGA_LONGITUDE_1, 
				  INVALID_GGA_LONGITUDE_2 })
	public void invalidGGALongitude(String invalidGGA) {
		TrackerUtils.convertToDecimalDegrees(invalidGGA);
	}
	
	@Test(expected = ParserTrackerException.class)
	@Parameters({ INVALID_GGA_1, INVALID_GGA_2, INVALID_GGA_3 })
	public void invalidGGA(String invalidGGA) {
		TrackerUtils.convertToDecimalDegrees(invalidGGA);
	}
}
