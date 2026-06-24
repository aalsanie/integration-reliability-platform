package io.github.aalsanie.irp.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<InboundEvent, UUID> {
}
