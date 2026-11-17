package com.github.skrip.prediction.decisionservice.model.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String message) {}
