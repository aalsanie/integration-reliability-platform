package io.github.aalsanie.irp.common.api;

import io.github.aalsanie.irp.connections.DuplicateConnectionException;
import io.github.aalsanie.irp.events.DuplicateInboundEventException;
import io.github.aalsanie.irp.events.EventNotFoundException;
import io.github.aalsanie.irp.events.IntegrationConnectionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {DuplicateConnectionException.class})
    public ResponseEntity<ApiErrorResponse> handleException(DuplicateConnectionException exception,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse response = new ApiErrorResponse(Instant.now(),
                status.value(),
                exception.getMessage(),
                status.getReasonPhrase(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(value = {IntegrationConnectionNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleException(IntegrationConnectionNotFoundException exception,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse response = new ApiErrorResponse(Instant.now(),
                status.value(),
                exception.getMessage(),
                status.getReasonPhrase(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {EventNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleException(EventNotFoundException exception,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse response = new ApiErrorResponse(Instant.now(),
                status.value(),
                exception.getMessage(),
                status.getReasonPhrase(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {DuplicateInboundEventException.class})
    public ResponseEntity<ApiErrorResponse> handleException(DuplicateInboundEventException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse response = new ApiErrorResponse(Instant.now(),
                status.value(),
                exception.getMessage(),
                status.getReasonPhrase(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
