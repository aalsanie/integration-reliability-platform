package io.github.aalsanie.irp.events;

import jakarta.validation.constraints.NotBlank;

public record UpdateEventStatusRequest(
        @NotBlank(message = "status must not be blank")
        String status
) {
}