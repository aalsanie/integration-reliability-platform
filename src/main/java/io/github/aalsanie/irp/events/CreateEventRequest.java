package io.github.aalsanie.irp.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record CreateEventRequest(
        @NotBlank(message = "externalEventId must not be blank")
        @Size(max = 200, message = "externalEventId must not exceed 200 characters")
        String externalEventId,

        @NotBlank(message = "eventType must not be blank")
        @Size(max = 100, message = "eventType must not exceed 100 characters")
        String eventType,

        @NotNull(message = "payload must not be null")
        JsonNode payload
        ) {
}
