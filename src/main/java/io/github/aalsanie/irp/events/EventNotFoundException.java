package io.github.aalsanie.irp.events;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("Event with id " + eventId + " not found");
    }
}
