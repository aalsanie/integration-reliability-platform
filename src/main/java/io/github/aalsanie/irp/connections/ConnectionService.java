package io.github.aalsanie.irp.connections;

import org.springframework.dao.DataIntegrityViolationException;
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
        String name = request.name().trim();
        String providerType = normalizeProviderType(request.providerType());

        if (repository.existsByNameAndProviderType(name, providerType)) {
            throw new DuplicateConnectionException("Connection already exists for provider type " + providerType);
        }

        IntegrationConnection connection = new IntegrationConnection(UUID.randomUUID(),
                name,
                providerType,
                ConnectionStatus.ACTIVE,
                Instant.now());

        try {
            IntegrationConnection savedConnection = repository.saveAndFlush(connection);
            return ConnectionResponse.from(savedConnection);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateConnectionException(
                    "A connection with this name and provider type already exists",
                    exception
            );
        }
    }

    private String normalizeProviderType(String providerType) {

        return providerType.trim().toUpperCase(Locale.ROOT);

    }
}