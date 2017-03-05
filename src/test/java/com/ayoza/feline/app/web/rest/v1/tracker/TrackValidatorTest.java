package com.ayoza.feline.app.web.rest.v1.tracker;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ayoza.com.feline.api.exceptions.FelineApiException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TrackValidatorTest {
	
	private static final Instant FROM_VALID = Instant.now();
	private static final Instant TO_VALID = FROM_VALID.plus(1, ChronoUnit.DAYS);
	private static final String ASC_VALID = "ASC";
	private static final String DESC_VALID = "DESC";
	private static final int PAGE_VALID = 1;
	private static final int NUM_REGISTERS_PER_PAGE_VALID = 10;
	
	private static final Instant FROM_AFTER_TO = TO_VALID.plus(1, ChronoUnit.DAYS);
	private static final String EMPTY_ORDER = "";
	private static final String ORDER_NULL = null;
	private static final String WRONG_ORDER = "XXX";
	private static final Integer PAGE_NULL = null;
	private static final Integer NEGATIVE_PAGE = -1;
	private static final Integer NUM_REGISTERS_NULL = null;
	private static final Integer NEGATIVE_NUM_REGISTERS = -1;
	
	private TrackValidator trackValidator = new TrackValidator();

	@Test
	@Parameters({ASC_VALID, DESC_VALID })
	public void allParametesAreValid(String order) throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, order, PAGE_VALID, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void fromIsOlderThanTo() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_AFTER_TO, TO_VALID, ASC_VALID, PAGE_VALID, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void orderIsEmpty() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, EMPTY_ORDER, PAGE_VALID, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void orderIsNull() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, ORDER_NULL, PAGE_VALID, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void orderIsWrong() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, WRONG_ORDER, PAGE_VALID, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void pageIsNull() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, DESC_VALID, PAGE_NULL, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void pageIsNegative() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, DESC_VALID, NEGATIVE_PAGE, NUM_REGISTERS_PER_PAGE_VALID);
	}
	
	@Test(expected = FelineApiException.class)
	public void numRegistersIsNull() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, DESC_VALID, PAGE_VALID, NUM_REGISTERS_NULL);
	}
	
	@Test(expected = FelineApiException.class)
	public void numRegistersIsNegative() throws FelineApiException {
		trackValidator.validateGetTracks(FROM_VALID, TO_VALID, DESC_VALID, PAGE_VALID, NEGATIVE_NUM_REGISTERS);
	}
}
