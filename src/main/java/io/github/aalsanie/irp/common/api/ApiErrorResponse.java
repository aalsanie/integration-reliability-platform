package io.github.aalsanie.irp.common.api;

import java.time.Instant;

public record ApiErrorResponse(Instant timestamp, int status, String message, String error, String path) {
}
