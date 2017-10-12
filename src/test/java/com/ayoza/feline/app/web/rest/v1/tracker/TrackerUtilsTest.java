package com.ayoza.feline.app.web.rest.v1.tracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import ayoza.com.feline.api.exceptions.FelineNoContentException;
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
	
	private final static PointDTO POINT_1 = PointDTO.builder().latitude(-10.0).longitude(-10.0).build();
	private final static PointDTO POINT_2 = PointDTO.builder().latitude(-2.0).longitude(-10.0).build();
	private final static PointDTO POINT_3 = PointDTO.builder().latitude(-10.0).longitude(-2.0).build();
	private final static PointDTO POINT_4 = PointDTO.builder().latitude(-2.0).longitude(-2.0).build();
	private final static List<PointDTO> COORDINATES = Arrays.asList(POINT_1, POINT_2, POINT_3, POINT_4);

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
	
	@Test
	public void givenCoordinates_whenGetCentralApiTraPoint_thenReturnsCentralPoint() {
		PointDTO center = TrackerUtils.getCentralApiTraPoint(COORDINATES);
		
		assertThat(center).isNotNull();
		assertThat(center.getLatitude()).isEqualTo(-6.0);
		assertThat(center.getLongitude()).isEqualTo(-6.0);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void givenNullListOfCoordinates_whenGetCentralApiTraPoint_thenThrowsFelineNoContentException() {
		TrackerUtils.getCentralApiTraPoint(null);
	}
	
	@Test(expected = FelineNoContentException.class)
	public void givenEmptyListOfCoordinates_whenGetCentralApiTraPoint_thenThrowsFelineNoContentException() {
		TrackerUtils.getCentralApiTraPoint(Collections.emptyList());
	}
}
