package io.github.aalsanie.irp.connections;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IntegrationConnectionRepository extends JpaRepository<IntegrationConnection, UUID> {
    boolean existsByNameAndProviderType(
            String name,
            String providerType
    );
}