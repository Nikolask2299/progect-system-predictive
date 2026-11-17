package com.github.skrip.prediction.decisionservice.exception;

import com.github.skrip.prediction.decisionservice.model.dto.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для REST API.
 * Возвращает корректные HTTP-коды: 400, 404, 409.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalTransitionException.class, TransitionGuardFailedException.class})
    public ResponseEntity<ErrorResponse> handleTransitionError(RuntimeException ex) {
        log.warn("Error handling transition: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(IllegalArgumentException ex) {
        log.warn("Error handling not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException ex) {
        log.warn("Optimistic locking: the alert was modified by another user");
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(HttpStatus.CONFLICT, "Alert was modified by another user"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Error handling data integrity violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(HttpStatus.CONFLICT, "Violation of uniqueness: the alert already exists"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
    }
}
