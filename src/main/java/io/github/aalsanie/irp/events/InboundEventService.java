package io.github.aalsanie.irp.events;

import io.github.aalsanie.irp.connections.IntegrationConnection;
import io.github.aalsanie.irp.connections.IntegrationConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class InboundEventService {

    private final EventRepository eventRepository;
    private final IntegrationConnectionRepository integrationConnectionRepository;

    public InboundEventService(EventRepository eventRepository,
                               IntegrationConnectionRepository integrationConnectionRepository) {
        this.eventRepository = eventRepository;
        this.integrationConnectionRepository = integrationConnectionRepository;
    }

    @Transactional
    public EventResponse createEvent(UUID connectionId, CreateEventRequest createEventRequest) {
        IntegrationConnection connection =
                integrationConnectionRepository.findById(connectionId)
                        .orElseThrow(() ->
                                new ConnectionNotFoundException(connectionId)
                        );
        String externalEventId = normalizeExternalEventId(createEventRequest.externalEventId());
        String eventType = normalizeEventType(createEventRequest.eventType());

        InboundEvent inboundEvent = new InboundEvent(UUID.randomUUID(),
                Instant.now(),
                ProcessingStatus.RECEIVED,
                createEventRequest.payload(),
                eventType,
                externalEventId,
                connection
                );
        InboundEvent savedInboundEvent = eventRepository.save(inboundEvent);
        return EventResponse.from(savedInboundEvent);
    }

    private String normalizeExternalEventId(String externalEventId) {
        return externalEventId.trim();
    }

    private String normalizeEventType(String eventType) {
        return eventType.trim();
    }

}
