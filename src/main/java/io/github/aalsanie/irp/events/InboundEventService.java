package io.github.aalsanie.irp.events;

import io.github.aalsanie.irp.common.api.PageResponse;
import io.github.aalsanie.irp.connections.IntegrationConnection;
import io.github.aalsanie.irp.connections.IntegrationConnectionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                                new IntegrationConnectionNotFoundException(connectionId)
                        );
        String externalEventId = normalizeExternalEventId(createEventRequest.externalEventId());
        String eventType = normalizeEventType(createEventRequest.eventType());

        boolean isDuplicate = eventRepository.existsByConnection_IdAndExternalEventId(connectionId, externalEventId);

        if (isDuplicate) {
            throw new DuplicateInboundEventException(connectionId, externalEventId);
        }

        InboundEvent inboundEvent = new InboundEvent(UUID.randomUUID(),
                Instant.now(),
                ProcessingStatus.RECEIVED,
                createEventRequest.payload(),
                eventType,
                externalEventId,
                connection
        );
        try {
            InboundEvent savedInboundEvent = eventRepository.saveAndFlush(inboundEvent);
            return EventResponse.from(savedInboundEvent);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateInboundEventException(connectionId, externalEventId, exception);
        }
    }

    public EventResponse updateEventStatus(UUID eventId, String processingStatus) {
        ProcessingStatus normalizedProcessingStatus = normalizeProcessingStatus(processingStatus);
        if(!eventRepository.existsById(eventId)){
            throw new EventNotFoundException(eventId);
        }
        InboundEvent event = eventRepository.updateInboundEvent(eventId, normalizedProcessingStatus);
        return EventResponse.from(event);
    }

    private ProcessingStatus normalizeProcessingStatus(String processingStatus) {
        processingStatus = processingStatus.trim().toUpperCase();
        return switch (processingStatus) {
            case "RECEIVED" -> ProcessingStatus.RECEIVED;
            case "COMPLETED" -> ProcessingStatus.COMPLETED;
            case "FAILED" -> ProcessingStatus.FAILED;
            case "PROCESSING" -> ProcessingStatus.PROCESSING;
            default -> throw new InvalidEventProcessingStatus(processingStatus);
        };
    }

    public EventResponse getEvent(UUID eventId) {
        return EventResponse.from(
                eventRepository.findById(eventId).orElseThrow(
                        () -> new EventNotFoundException(eventId)));
    }

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getEvents(UUID connectionId, int page, int size) {

        integrationConnectionRepository.findById(connectionId)
                .orElseThrow(() ->
                        new IntegrationConnectionNotFoundException(connectionId)
                );
        Pageable pageable = PageRequest.of(page, size,Sort.by(
                Sort.Order.desc("receivedAt"),
                Sort.Order.desc("id")
        ));
        Page<InboundEvent> eventPage =
                eventRepository.findByConnection_Id(connectionId, pageable);

        Page<EventResponse> responsePage =
                eventPage.map(EventResponse::from);

        return PageResponse.from(responsePage);
    }

    private String normalizeExternalEventId(String externalEventId) {
        return externalEventId.trim();
    }

    private String normalizeEventType(String eventType) {
        return eventType.trim();
    }

}
