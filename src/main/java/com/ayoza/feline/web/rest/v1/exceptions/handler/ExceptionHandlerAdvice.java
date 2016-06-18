package com.ayoza.feline.web.rest.v1.exceptions.handler;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ayoza.com.feline.api.exceptions.FelineApiException;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	
	private Logger logger = Logger.getLogger(ExceptionHandlerAdvice.class); 

	@ExceptionHandler(FelineApiException.class)
    public ResponseEntity<String> handleFelineApiException(FelineApiException e) {
        
    	logger.debug("Exception ocurred: " + e.getMessage());
    	
		HttpHeaders headers = new HttpHeaders();
		MediaType media = MediaType.valueOf("application/json;charset=UTF-8");
		headers.setContentType(media);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		
		objectNode.put("code", e.getError());
		objectNode.put("msg", e.getMessage());
    	
    	return new ResponseEntity<String>(objectNode.toString(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
      
}