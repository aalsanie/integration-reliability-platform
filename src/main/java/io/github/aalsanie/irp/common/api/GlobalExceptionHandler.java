package io.github.aalsanie.irp.common.api;

import io.github.aalsanie.irp.connections.DuplicateConnectionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {DuplicateConnectionException.class})
    public ResponseEntity<ApiErrorResponse> handleException(DuplicateConnectionException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse response = new ApiErrorResponse(Instant.now(),
                status.value(),
                exception.getMessage(),
                status.getReasonPhrase(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
