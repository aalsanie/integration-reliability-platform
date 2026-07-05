package io.github.aalsanie.irp;

import io.github.aalsanie.irp.connections.ConnectionStatus;
import io.github.aalsanie.irp.connections.IntegrationConnection;
import io.github.aalsanie.irp.connections.IntegrationConnectionRepository;
import io.github.aalsanie.irp.events.EventRepository;
import io.github.aalsanie.irp.events.InboundEvent;
import io.github.aalsanie.irp.events.ProcessingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class EventPaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IntegrationConnectionRepository integrationConnectionRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        integrationConnectionRepository.deleteAll();
    }

    @Test
    void shouldReturnRequestedPageOrderedNewestFirstAndIsolatedByConnection()
            throws Exception {

        IntegrationConnection connectionA =
                saveConnection("connection-a", "provider-a");

        IntegrationConnection connectionB =
                saveConnection("connection-b", "provider-b");

        UUID event1 = uuid(1);
        UUID event2 = uuid(2);
        UUID event3 = uuid(3);
        UUID event4 = uuid(4);
        UUID event5 = uuid(5);

        saveEvent(
                connectionA,
                event1,
                "event-1",
                Instant.parse("2026-07-05T10:00:00Z")
        );

        saveEvent(
                connectionA,
                event2,
                "event-2",
                Instant.parse("2026-07-05T11:00:00Z")
        );

        saveEvent(
                connectionA,
                event3,
                "event-3",
                Instant.parse("2026-07-05T12:00:00Z")
        );

        saveEvent(
                connectionA,
                event4,
                "event-4",
                Instant.parse("2026-07-05T13:00:00Z")
        );

        saveEvent(
                connectionA,
                event5,
                "event-5",
                Instant.parse("2026-07-05T14:00:00Z")
        );

        // Must never appear in connection A's results.
        saveEvent(
                connectionB,
                uuid(100),
                "other-connection-event",
                Instant.parse("2026-07-05T15:00:00Z")
        );

        /*
         * Complete ordering for connection A:
         *
         * page 0: event5, event4
         * page 1: event3, event2
         * page 2: event1
         */
        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connectionA.getId())
                                .param("page", "1")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].eventId")
                        .value(event3.toString()))
                .andExpect(jsonPath("$.items[1].eventId")
                        .value(event2.toString()))
                .andExpect(jsonPath("$.items[0].connectionId")
                        .value(connectionA.getId().toString()))
                .andExpect(jsonPath("$.items[1].connectionId")
                        .value(connectionA.getId().toString()))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void shouldUseEventIdAsTieBreakerWhenReceivedTimesAreEqual()
            throws Exception {

        IntegrationConnection connection =
                saveConnection("tie-breaker", "test-provider");

        UUID lowerId = uuid(10);
        UUID higherId = uuid(11);
        UUID olderId = uuid(9);

        Instant sameTimestamp =
                Instant.parse("2026-07-05T12:00:00Z");

        saveEvent(
                connection,
                lowerId,
                "same-time-lower-id",
                sameTimestamp
        );

        saveEvent(
                connection,
                higherId,
                "same-time-higher-id",
                sameTimestamp
        );

        saveEvent(
                connection,
                olderId,
                "older-event",
                Instant.parse("2026-07-05T11:00:00Z")
        );

        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].eventId")
                        .value(higherId.toString()))
                .andExpect(jsonPath("$.items[1].eventId")
                        .value(lowerId.toString()))
                .andExpect(jsonPath("$.items[2].eventId")
                        .value(olderId.toString()));
    }

    @Test
    void shouldReturnEmptyPageForExistingConnectionUsingDefaultPagination()
            throws Exception {

        IntegrationConnection connection =
                saveConnection("empty-connection", "test-provider");

        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldReturnNotFoundWhenConnectionDoesNotExist()
            throws Exception {

        UUID missingConnectionId = UUID.randomUUID();

        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                missingConnectionId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error")
                        .value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/connections/"
                                + missingConnectionId
                                + "/events"))
                .andExpect(jsonPath("$.message")
                        .value(containsString(
                                missingConnectionId.toString()
                        )));
    }

    @Test
    void shouldAcceptPaginationBoundaryValues()
            throws Exception {

        IntegrationConnection connection =
                saveConnection("boundaries", "test-provider");

        saveEvent(
                connection,
                uuid(20),
                "boundary-event",
                Instant.parse("2026-07-05T10:00:00Z")
        );

        // Minimum allowed size.
        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                                .param("page", "0")
                                .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.items", hasSize(1)));

        // Maximum allowed size.
        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                                .param("page", "0")
                                .param("size", "100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 20",
            "0, 0",
            "0, 101"
    })
    void shouldRejectPaginationValuesOutsideAllowedRange(
            int page,
            int size
    ) throws Exception {

        IntegrationConnection connection =
                saveConnection("invalid-" + page + "-" + size,
                        "test-provider");

        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "page, not-a-number",
            "size, not-a-number"
    })
    void shouldRejectNonNumericPaginationParameters(
            String parameter,
            String value
    ) throws Exception {

        IntegrationConnection connection =
                saveConnection("invalid-type-" + parameter,
                        "test-provider");

        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                                .param(parameter, value)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyItemsWhenRequestedPageIsBeyondAvailableResults()
            throws Exception {

        IntegrationConnection connection =
                saveConnection("out-of-range-page", "test-provider");

        saveEvent(
                connection,
                uuid(30),
                "only-event",
                Instant.parse("2026-07-05T10:00:00Z")
        );

        mockMvc.perform(
                        get("/api/v1/connections/{connectionId}/events",
                                connection.getId())
                                .param("page", "5")
                                .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.page").value(5))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    private IntegrationConnection saveConnection(
            String name,
            String providerType
    ) {
        IntegrationConnection connection =
                new IntegrationConnection(
                        UUID.randomUUID(),
                        name,
                        providerType,
                        ConnectionStatus.ACTIVE,
                        Instant.parse("2026-07-05T09:00:00Z")
                );

        return integrationConnectionRepository.saveAndFlush(connection);
    }

    private void saveEvent(
            IntegrationConnection connection,
            UUID eventId,
            String externalEventId,
            Instant receivedAt
    ) {
        InboundEvent event =
                new InboundEvent(
                        eventId,
                        receivedAt,
                        ProcessingStatus.RECEIVED,
                        jsonMapper.createObjectNode(),
                        "integration.test",
                        externalEventId,
                        connection
                );

        eventRepository.saveAndFlush(event);
    }

    private static UUID uuid(long value) {
        return UUID.fromString(
                "00000000-0000-0000-0000-%012d".formatted(value)
        );
    }
}