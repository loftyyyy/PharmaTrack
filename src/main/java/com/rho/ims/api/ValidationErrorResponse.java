package com.rho.ims.api;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        Instant timestamp,
        HttpStatus status,
        String error,
        String message,
        Map<String, String> fieldErrors,
        String path
) {}
