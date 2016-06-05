package com.ayoza.feline.app.web.rest.utils.v1;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	
	private Logger logger = Logger.getLogger(ExceptionHandlerAdvice.class); 

	/*
    @ExceptionHandler(ChekingCredentialsFailedException.class)
    public ResponseEntity handleException(ChekingCredentialsFailedException e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Error Message");
    } 
    */       
}