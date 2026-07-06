package io.github.aalsanie.irp.events;

import io.github.aalsanie.irp.common.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class EventController {

    private final InboundEventService inboundEventService;

    public EventController(InboundEventService inboundEventService) {
        this.inboundEventService = inboundEventService;
    }

    @PostMapping("/api/v1/connections/{connectionId}/events")
    public ResponseEntity<EventResponse> createEvent(@PathVariable("connectionId") UUID connectionId,
                                                     @Valid
                                                     @RequestBody CreateEventRequest event) {
        EventResponse eventResponse = inboundEventService.createEvent(connectionId, event);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }

    @PostMapping("/api/v1/events/{eventId}/status")
    public ResponseEntity<EventResponse> createEvent(@PathVariable("eventId") UUID eventId,
                                                     @RequestBody String status) {
        EventResponse eventResponse = inboundEventService.updateEventStatus(eventId, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }

    @GetMapping("/api/v1/connections/{connectionId}/events")
    public ResponseEntity<PageResponse<EventResponse>> getEvents(@PathVariable("connectionId") UUID connectionId,
                                                     @Valid @ModelAttribute GetEventsRequest request) {
        PageResponse<EventResponse> eventResponse = inboundEventService.getEvents(connectionId, request.page(), request.size());
        return ResponseEntity.status(HttpStatus.OK).body(eventResponse);
    }

    @GetMapping("/api/v1/events/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable("eventId") UUID eventId) {
        EventResponse eventResponse = inboundEventService.getEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventResponse);
    }
}
