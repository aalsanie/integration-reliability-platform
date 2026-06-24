package io.github.aalsanie.irp.events;

import java.util.UUID;

public class ConnectionNotFoundException extends RuntimeException {

    public ConnectionNotFoundException(UUID connectionId) {
        super("Connection with id " + connectionId + " not found");
    }

}
