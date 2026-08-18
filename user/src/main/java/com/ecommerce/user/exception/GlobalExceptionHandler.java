package com.ecommerce.user.exception;

import com.ecommerce.user.dto.ErrorResponse;
import com.ecommerce.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCreds(BadCredentialsException e, HttpServletRequest request){
        logger.warn("BadCredentialsException for URI: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                LocalDateTime.now(), 401,
                "INVALID_CREDENTIALS",
                e.getMessage(),
                request.getRequestURI()
        ));
    }
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> authCredsNotFound(AuthenticationCredentialsNotFoundException e, HttpServletRequest request){
        logger.warn("AUTHENTICATION_FAILED for URI: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                LocalDateTime.now(), 401,
                "AUTHENTICATION_FAILED",
                "Authentication failed",
                request.getRequestURI()
        ));
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotReceived(EntityNotFoundException e, HttpServletRequest request){
        logger.warn("EntityNotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                LocalDateTime.now(), 404,
                "ENTITY_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        ));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> accessAbsent(MethodArgumentNotValidException e, HttpServletRequest request){
        logger.warn("AccessDeniedException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                LocalDateTime.now(), 400,
                "VALIDATION_FAILED",
                "Inputs provided are not valid as per the validations provided",
                request.getRequestURI()
        ));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessAbsent(AccessDeniedException e, HttpServletRequest request){
        logger.warn("AccessDeniedException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                LocalDateTime.now(), 403,
                "ACCESS_DENIED",
                e.getMessage(),
                request.getRequestURI()
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {
        logger.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        LocalDateTime.now(), 500,
                        "INTERNAL_ERROR",
                        "Something went wrong. Please try again later.",
                        request.getRequestURI()
                ));
    }
}

