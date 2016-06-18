package com.ayoza.feline.web.rest.v1.exceptions;

import ayoza.com.feline.api.exceptions.FelineApiException;

public class ParserTrackerException extends FelineApiException {

	private static final long serialVersionUID = -4738453754063071235L;
	
	public static final int WRONG_GPGGA_FORMAT = 1;
	public static final String WRONG_GPGGA_FORMAT_MSG = "Input received has a wrong GPGGA format";

	public ParserTrackerException(int error, String message, Throwable e) {
		super(error, message, e);
	}

}
