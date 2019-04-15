package com.redactor.redactor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by NP on 4/15/2019.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="IOException occured")
    @ExceptionHandler(IOException.class)
    public void handleIOException(Exception ex){
        logger.error("IOException handler executed due to::" + ex.getMessage());
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="IOException occured")
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex){
        logger.error("Exception occured. Reason::" + ex.getMessage());
    }
}
