package io.github.aalsanie.irp.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<InboundEvent, UUID> {
    boolean existsByConnection_IdAndExternalEventId( UUID connectionId, String externalEventId );
    Page<InboundEvent> findByConnection_Id( UUID connectionId, Pageable pageable);
}
