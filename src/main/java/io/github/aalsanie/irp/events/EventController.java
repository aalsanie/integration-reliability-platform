package io.github.aalsanie.irp.events;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/connections/{connectionId}/events")
public class EventController {

    private final InboundEventService inboundEventService;

    public EventController(InboundEventService inboundEventService) {
        this.inboundEventService = inboundEventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@PathVariable("connectionId") UUID connectionId,
                                                     @Valid
                                                     @RequestBody CreateEventRequest event) {
        EventResponse eventResponse = inboundEventService.createEvent(connectionId, event);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }
}
