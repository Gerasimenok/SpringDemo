package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleNumberFormatException(NumberFormatException ex) {
        ModelAndView modelAndView = new ModelAndView("error/400");
        modelAndView.addObject("errorMessage", "Неверный формат числа");
        return modelAndView;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleResourceNotFoundException(ResourceNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleSecurityException(SecurityException ex) {
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneralException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("errorMessage", "Произошла непредвиденная ошибка: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFoundException() {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("errorMessage", "Страница не найдена");
        return modelAndView;
    }
}