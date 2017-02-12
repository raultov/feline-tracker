package com.ayoza.feline.web.rest.v1.exceptions;

import ayoza.com.feline.api.exceptions.FelineApiException;

public class ParserTrackerException extends FelineApiException {

	private static final long serialVersionUID = -4738453754063071235L;
	
	public static final int WRONG_GPGGA_FORMAT = 1;
	public static final String WRONG_GPGGA_FORMAT_MSG = "Input received has a wrong GPGGA format";
	
	public static final int WRONG_DATES_VALUES = 2;
	public static final String WRONG_DATES_VALUES_MSG = "Date From cannot be older than date To";
	
	public static final int WRONG_ORDER_VALUE = 3;
	public static final String WRONG_ORDER_VALUE_MSG = "Order field must be either ASC or DESC";
	
	public static final int ERROR_ORDER_CANNOT_BE_EMPTY = 4;
	public static final String ERROR_ORDER_CANNOT_BE_EMPTY_MSG = "Order field cannot be empty";
	
	public static final int WRONG_PAGE_NEGATIVE_VALUE = 5;
	public static final String WRONG_PAGE_NEGATIVE_VALUE_MSG = "Page field cannot be negative";
	
	public static final int ERROR_PAGE_CANNOT_BE_NULL = 6;
	public static final String ERROR_PAGE_CANNOT_BE_NULL_MSG = "Page field cannot be null";
	
	public static final int WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE = 7;
	public static final String WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE_MSG = "The number of registers per page cannot be negative";
	
	public static final int ERROR_REGISTERS_PER_PAGE_CANNOT_BE_NULL = 8;
	public static final String ERROR_REGISTERS_PER_PAGE_CANNOT_BE_NULL_MSG = "The number of registers per page cannot be null";

	public ParserTrackerException(int error, String message, Throwable e) {
		super(error, message, e);
	}

}
