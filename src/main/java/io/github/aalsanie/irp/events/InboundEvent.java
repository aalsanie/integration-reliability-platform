package io.github.aalsanie.irp.events;

import io.github.aalsanie.irp.connections.IntegrationConnection;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbound_event")
public class InboundEvent {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connection_id", nullable = false)
    private IntegrationConnection connection;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private ProcessingStatus status;

    @Column(name = "external_event_id", nullable = false, length = 200)
    private String externalEventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    public InboundEvent() {
        //Required by JPA
    }

    public InboundEvent(UUID id,
                        Instant receivedAt,
                        ProcessingStatus status,
                        JsonNode payload,
                        String eventType,
                        String externalEventId,
                        IntegrationConnection connection) {
        this.id = id;
        this.receivedAt = receivedAt;
        this.status = status;
        this.payload = payload;
        this.eventType = eventType;
        this.externalEventId = externalEventId;
        this.connection = connection;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public String getEventType() {
        return eventType;
    }

    public String getExternalEventId() {
        return externalEventId;
    }

    public IntegrationConnection getConnection() {
        return connection;
    }

    public UUID getId() {
        return id;
    }
}
