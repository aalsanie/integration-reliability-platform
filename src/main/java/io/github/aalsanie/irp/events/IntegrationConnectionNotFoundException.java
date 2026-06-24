package io.github.aalsanie.irp.events;

import java.util.UUID;

public class IntegrationConnectionNotFoundException extends RuntimeException {

    public IntegrationConnectionNotFoundException(UUID connectionId) {
        super("Connection with id " + connectionId + " not found");
    }

}
