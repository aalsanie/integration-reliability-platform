package io.github.aalsanie.irp.connections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class ConnectionService {
    private final IntegrationConnectionRepository repository;

    public ConnectionService(IntegrationConnectionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ConnectionResponse createConnection(CreateConnectionRequest request) {

        IntegrationConnection connection = new IntegrationConnection(UUID.randomUUID(),
                request.name().trim(),
                normalizeProviderType(request.providerType()),
                ConnectionStatus.ACTIVE, Instant.now());

        IntegrationConnection savedConnection = repository.save(connection);

        return ConnectionResponse.from(savedConnection);
    }

    private String normalizeProviderType(String providerType) {

        return providerType.trim().toUpperCase(Locale.ROOT);

    }
}