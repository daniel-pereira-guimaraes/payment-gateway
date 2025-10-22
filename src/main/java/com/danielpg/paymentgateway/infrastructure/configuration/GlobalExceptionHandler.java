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
    public ResponseEntity<AppErrorResponse> handlePublicException(
            IllegalStateException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.badRequest().body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AppErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<AppErrorResponse> handleAccessForbiddenException(
            AccessForbiddenException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AppErrorResponse(ex));
    }


    @ExceptionHandler(AbstractNotFoundException.class)
    public ResponseEntity<AppErrorResponse> handleAbstractNotFoundException(
            AbstractNotFoundException ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AppErrorResponse(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppErrorResponse> handleException(Exception ex, WebRequest request) {
        logError(ex);
        return ResponseEntity.internalServerError().body(new AppErrorResponse("Erro inesperado no servidor."));
    }

    private void logError(Exception e) {
        LOGGER.error(logMessage(e), e);
    }

    private String logMessage(Exception e) {
        var message = e.getMessage();
        var className = e.getClass().getName();
        return message == null ? className : className + ": " + message;
    }
}