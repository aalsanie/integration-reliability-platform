package io.github.aalsanie.irp.events;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(UUID eventId,
                            UUID connectionId,
                            String externalEventId,
                            String eventType,
                            ProcessingStatus status,
                            Instant receivedAt) {

    public static EventResponse from(InboundEvent inboundEvent) {
        return new EventResponse(inboundEvent.getId(),
                inboundEvent.getConnection().getId(),
                inboundEvent.getExternalEventId(),
                inboundEvent.getEventType(),
                inboundEvent.getStatus(),
                inboundEvent.getReceivedAt());
    }

}
