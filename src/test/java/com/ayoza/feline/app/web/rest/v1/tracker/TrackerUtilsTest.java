package com.ayoza.feline.app.web.rest.v1.tracker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TrackerUtilsTest {

	@Test
	@Parameters({ "4025.7313N, 40.4289", 
	              "4025.7313S, -40.4289",
	              "04025.7313N, 40.4289",})
	public void validGGALatitude(String gga, double expected) {
		assertThat(TrackerUtils.convertToDecimalDegrees(gga)).isEqualTo(expected);
	}
	
	@Test(expected = ParserTrackerException.class)
	@Parameters({ "A4025.7313N",
	              "4025.C7313S" })
	public void invalidGGALatitude(String invalidGGA) {
		TrackerUtils.convertToDecimalDegrees(invalidGGA);
	}
	
	@Test
	@Parameters({ "00338.5613W, -3.6427", 
	              "00338.5613E, 3.6427",
	              "338.5613E, 3.6427"})
	public void validGGALongitude(String gga, double expected) {
		assertThat(TrackerUtils.convertToDecimalDegrees(gga)).isEqualTo(expected);
	}
	
	@Test(expected = ParserTrackerException.class)
	@Parameters({ "A00338.5613W", 
	              "00338.T5613E" })
	public void invalidGGALongitude(String invalidGGA) {
		TrackerUtils.convertToDecimalDegrees(invalidGGA);
	}
	
	@Test(expected = ParserTrackerException.class)
	@Parameters({ "4025.7313R", "00338.5613", "AAAAAAAA" })
	public void invalidGGA(String invalidGGA) {
		TrackerUtils.convertToDecimalDegrees(invalidGGA);
	}
}
