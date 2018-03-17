package com.ayoza.feline.app.web.rest.v1.tracker;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;

@Service
class TrackValidator {
	
	private static final String ASC = "ASC";
	private static final String DESC = "DESC";
	
	public void validateGetTracks(Instant startDateFrom, Instant startDateTo, String orderAscDesc, Integer page,
			Integer numRegistersPerPage) {

		if (startDateFrom != null && startDateTo != null) {
			if (startDateFrom.isAfter(startDateTo)) {
				throw new ParserTrackerException(ParserTrackerException.WRONG_DATES_VALUES,
						ParserTrackerException.WRONG_DATES_VALUES_MSG,
						new Exception(ParserTrackerException.WRONG_DATES_VALUES_MSG));
			}
		}

		if (orderAscDesc == null || orderAscDesc.isEmpty()) {
			throw new ParserTrackerException(ParserTrackerException.ERROR_ORDER_CANNOT_BE_EMPTY,
					ParserTrackerException.ERROR_ORDER_CANNOT_BE_EMPTY_MSG,
					new Exception(ParserTrackerException.ERROR_ORDER_CANNOT_BE_EMPTY_MSG));
		} else if (!ASC.equals(orderAscDesc) && !DESC.equals(orderAscDesc)) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_ORDER_VALUE,
					ParserTrackerException.WRONG_ORDER_VALUE_MSG,
					new Exception(ParserTrackerException.WRONG_ORDER_VALUE_MSG));
		}

		if (page == null) {
			throw new ParserTrackerException(ParserTrackerException.ERROR_PAGE_CANNOT_BE_NULL,
					ParserTrackerException.ERROR_PAGE_CANNOT_BE_NULL_MSG,
					new Exception(ParserTrackerException.ERROR_PAGE_CANNOT_BE_NULL_MSG));
		} else if (page < 0) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_PAGE_NEGATIVE_VALUE,
					ParserTrackerException.WRONG_PAGE_NEGATIVE_VALUE_MSG,
					new Exception(ParserTrackerException.WRONG_PAGE_NEGATIVE_VALUE_MSG));
		}

		if (numRegistersPerPage == null) {
			throw new ParserTrackerException(ParserTrackerException.ERROR_REGISTERS_PER_PAGE_CANNOT_BE_NULL,
					ParserTrackerException.ERROR_REGISTERS_PER_PAGE_CANNOT_BE_NULL_MSG,
					new Exception(ParserTrackerException.ERROR_REGISTERS_PER_PAGE_CANNOT_BE_NULL_MSG));
		} else if (numRegistersPerPage < 0) {
			throw new ParserTrackerException(ParserTrackerException.WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE,
					ParserTrackerException.WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE_MSG,
					new Exception(ParserTrackerException.WRONG_REGISTERS_PER_PAGE_NEGATIVE_VALUE_MSG));
		}
	}
}
