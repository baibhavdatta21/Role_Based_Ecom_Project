package com.ecommerce.order.exception;

import com.ecommerce.order.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
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
    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ErrorResponse> handleDownstream(
            DownstreamServiceException ex,
            HttpServletRequest request) {

        logger.error("Downstream service failure at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        503,
                        "DOWNSTREAM_SERVICE_UNAVAILABLE",
                        ex.getMessage(),
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
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request){
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

