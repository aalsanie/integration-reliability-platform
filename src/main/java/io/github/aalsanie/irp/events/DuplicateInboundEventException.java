package io.github.aalsanie.irp.events;

import java.util.UUID;

public class DuplicateInboundEventException extends RuntimeException {
    public DuplicateInboundEventException(UUID connectionId, String externalEventId) {
        super("Duplicate inbound event where connectionId: " + connectionId +
                " and externalEventId: " + externalEventId);
    }

    public DuplicateInboundEventException(UUID connectionId, String externalEventId, Throwable cause) {
        super("Duplicate inbound event where connectionId: " + connectionId +
                " and externalEventId: " + externalEventId, cause);
    }
}
