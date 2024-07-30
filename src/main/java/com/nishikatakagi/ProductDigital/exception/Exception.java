package com.nishikatakagi.ProductDigital.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class Exception {
	@ExceptionHandler(NoResourceFoundException.class)
	public String handlePageNotFoundError() {
        return "publics/404.html";
    }
}
