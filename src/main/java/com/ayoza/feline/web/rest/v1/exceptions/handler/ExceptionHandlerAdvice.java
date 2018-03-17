package com.ayoza.feline.web.rest.v1.exceptions.handler;

import static org.springframework.http.HttpStatus.*;

import com.ayoza.feline.web.rest.v1.exceptions.ParserTrackerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ayoza.com.feline.api.exceptions.FelineApiException;
import ayoza.com.feline.api.exceptions.FelineNoContentException;
import ayoza.com.feline.api.exceptions.TrackerException;
import ayoza.com.feline.api.exceptions.UserServicesException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
	
	private static final String MEDIA_TYPE_JSON = "application/json;charset=UTF-8";
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final HttpHeaders headers;
	
	static {
		headers = new HttpHeaders();
		MediaType media = MediaType.valueOf(MEDIA_TYPE_JSON);
		headers.setContentType(media);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedExceptionn(AccessDeniedException e) {        
    	log.error("Exception ocurred: {}", e.getMessage());
    	return new ResponseEntity<String>(objectNode(null, e.getMessage()), headers, FORBIDDEN);
    }
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleNotSupportedMethodExceptionn(HttpRequestMethodNotSupportedException e) {        
    	log.error("Exception ocurred: {}", e.getMessage());
    	return new ResponseEntity<String>(objectNode(null, e.getMessage()), headers, METHOD_NOT_ALLOWED);
    }

	@ExceptionHandler(ParserTrackerException.class)
    public ResponseEntity<String> handleParserTrackerExceptionn(ParserTrackerException e) {        
    	log.error("Exception ocurred: {}", e.getMessage());
    	return new ResponseEntity<String>(objectNode(e), headers, BAD_REQUEST);
    }
      
	@ExceptionHandler(FelineNoContentException.class)
	public ResponseEntity<String> handleFelineNoContentException(FelineNoContentException e) {
		log.error("Exception ocurred: {}", e.getMessage());
		return new ResponseEntity<String>(objectNode(e), headers, NO_CONTENT);
	}
	
	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<String> handleUnauthorizedUserException(UnauthorizedUserException e) {
		log.error("Exception ocurred: {}", e.getMessage());
		return new ResponseEntity<String>(objectNode(UNAUTHORIZED.value(), e.getMessage()), headers, UNAUTHORIZED);
	}
	
	@ExceptionHandler(UserServicesException.class)
	public ResponseEntity<String> handleUserServicesException(UserServicesException e) {
		log.error("Exception ocurred: {}", e.getMessage());
		return new ResponseEntity<String>(objectNode(e), headers, UNAUTHORIZED);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
		log.error("Exception ocurred: {}", e.getMessage());
		return new ResponseEntity<String>(objectNode(UNAUTHORIZED.value(), e.getMessage()), headers, UNAUTHORIZED);
	}
	
	@ExceptionHandler(TrackerException.class)
	public ResponseEntity<String> handleTrackerException(TrackerException e) {
		log.error("Exception ocurred: {}", e.getMessage());
		return new ResponseEntity<String>(objectNode(e), headers, NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception e) {
		log.error("Exception ocurred: {}", e.getMessage());
		return new ResponseEntity<String>(objectNode(INTERNAL_SERVER_ERROR.value(), e.getMessage()), headers, INTERNAL_SERVER_ERROR);
	}
	
	private String objectNode(Integer error, String message) {
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put("code", error);
		objectNode.put("msg", message);
		
		return objectNode.toString();
	}
	
	private String objectNode(FelineApiException e) {
		return objectNode(e.getError(), e.getMessage());
	}
}