package io.github.aalsanie.irp.connections;

import java.util.UUID;

public record ConnectionResponse(UUID id, String name, String providerType, ConnectionStatus status) {
    public static ConnectionResponse from(IntegrationConnection connection) {
        return new ConnectionResponse(connection.getId(),
                connection.getName(),
                connection.getProviderType(),
                connection.getStatus());
    }
}