package com.ciberbus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NegocioException.class)
    public String negocio(NegocioException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String general(Exception ex, Model model) {
        log.error("Error no controlado", ex);
        model.addAttribute("mensaje", "Ocurrió un error. Intente de nuevo.");
        return "error";
    }
}
