package io.github.aalsanie.irp.events;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record GetEventsRequest(
        @Min(value = 0, message = "page must be greater than or equal to 0")
        int page,

        @Min(value = 1, message = "size must be at least 1")
        @Max(value = 100, message = "size must not exceed 100")
        int size
) {
}
