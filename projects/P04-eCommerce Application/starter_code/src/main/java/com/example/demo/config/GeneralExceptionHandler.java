package com.example.demo.config;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {
    @Autowired
    Logger logger;

    @ExceptionHandler(Throwable.class)
    public void handleException() {
        logger.error("Exception error occured in project.");
    }
}