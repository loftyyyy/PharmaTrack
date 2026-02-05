package com.rho.ims.api.exception;

import com.rho.ims.api.ErrorResponse;
import com.rho.ims.api.ValidationErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                "validation error",
                "invalid field(s)",
                fieldErrors,
                req.getRequestURI()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "data integrity violation",
                ex.getMostSpecificCause().getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicateCredentialException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateCredential(DuplicateCredentialException ex, HttpServletRequest req) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "duplicate resource",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "resource not found",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleBusinessViolation(BusinessRuleViolationException ex, HttpServletRequest req) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "business rule violation",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAny(Exception ex, HttpServletRequest req) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "internal error",
                ex.getMessage(),
                req.getRequestURI()
        );
    }








































//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ValidationErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
//        Map<String, String> fieldErrors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .collect(Collectors.toMap(
//                        FieldError::getField,
//                        FieldError::getDefaultMessage,
//                        (oldVal, __) -> oldVal
//                ));
//
//        return new ValidationErrorResponse(
//                Instant.now(),
//                HttpStatus.BAD_REQUEST,
//                "validation-error",
//                "Some fields are invalid",
//                fieldErrors,
//                req.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler(DuplicateKeyException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleDuplicateKey(DuplicateKeyException ex, HttpServletRequest req) {
//        return new ErrorResponse(Instant.now(), 409, "Duplicate Key", ex.getMessage(), req.getRequestURI());
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
//        return new ErrorResponse(Instant.now(), 404, "not-found", ex.getMessage(), req.getRequestURI());
//    }
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleIntegrityError(DataIntegrityViolationException ex, HttpServletRequest req) {
//        return new ErrorResponse(Instant.now(), 409, "constraint-violation",
//                "Database constraint was violated", req.getRequestURI());
//    }
//
}
