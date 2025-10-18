package com.danielpg.paymentgateway.infrastructure.configuration;

import com.danielpg.paymentgateway.application.auth.AccessForbiddenException;
import com.danielpg.paymentgateway.application.auth.InvalidCredentialsException;
import com.danielpg.paymentgateway.domain.shared.AbstractNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handlePublicException(
            IllegalStateException ex, WebRequest request) {
        LOGGER.error(logMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        LOGGER.error(logMessage(ex));
        return ResponseEntity.badRequest().body(new ErrorResponse(ex));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException e, WebRequest request) {
        LOGGER.error(logMessage(e));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e));
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleAccessForbiddenException(
            AccessForbiddenException ex, WebRequest request) {
        LOGGER.error(logMessage(ex));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex));
    }


    @ExceptionHandler(AbstractNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAbstractNotFoundException(
            AbstractNotFoundException ex, WebRequest request) {
        LOGGER.error(logMessage(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        LOGGER.error(logMessage(ex));
        return ResponseEntity.internalServerError().body(new ErrorResponse("Erro inesperado no servidor."));
    }


    private String logMessage(Exception e) {
        var message = e.getMessage();
        var className = e.getClass().getName();
        return message == null ? className : className + ": " + message;
    }
}