package io.github.aalsanie.irp.connections;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "integration_connection")
public class IntegrationConnection {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "provider_type", nullable = false, length = 50)
    private String providerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConnectionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected IntegrationConnection() {
        // Required by JPA.
    }

    public IntegrationConnection(
            UUID id,
            String name,
            String providerType,
            ConnectionStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.providerType = providerType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProviderType() {
        return providerType;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}