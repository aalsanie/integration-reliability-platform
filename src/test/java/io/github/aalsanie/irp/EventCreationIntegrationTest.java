package io.github.aalsanie.irp;

import io.github.aalsanie.irp.common.api.ApiErrorResponse;
import io.github.aalsanie.irp.connections.ConnectionStatus;
import io.github.aalsanie.irp.connections.IntegrationConnection;
import io.github.aalsanie.irp.connections.IntegrationConnectionRepository;
import io.github.aalsanie.irp.events.EventRepository;
import io.github.aalsanie.irp.events.EventResponse;
import io.github.aalsanie.irp.events.InboundEvent;
import io.github.aalsanie.irp.events.ProcessingStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class EventCreationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IntegrationConnectionRepository integrationConnectionRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    public void setup() {
        integrationConnectionRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void validEventCreationTest() throws Exception {

        IntegrationConnection connection = new IntegrationConnection(UUID.randomUUID(),
                "test",
                "test",
                ConnectionStatus.ACTIVE,
                Instant.now());

        IntegrationConnection savedConnection = integrationConnectionRepository.save(connection);

        MvcResult result = mockMvc.perform(post("/api/v1/connections/" + savedConnection.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {\s
                                   "externalEventId": "evt_12345",\s
                                   "eventType": "payment.succeeded",\s
                                   "payload":\s
                                   {\s
                                     "paymentId": "pay_100",\s
                                     "amount": 2500,\s
                                     "currency": "USD"\s
                                   }\s
                                 }
                                \s"""))
                .andExpect(status().isCreated())
                .andReturn();

        EventResponse eventResponse = jsonMapper.readValue(result.getResponse().getContentAsString(), EventResponse.class);
        Assertions.assertNotNull(eventResponse);
        Assertions.assertEquals("payment.succeeded", eventResponse.eventType());
        Assertions.assertEquals("evt_12345", eventResponse.externalEventId());
        Assertions.assertEquals(ProcessingStatus.RECEIVED, eventResponse.status());
        List<InboundEvent> events = eventRepository.findAll();
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        InboundEvent inboundEvent = events.getFirst();
        Assertions.assertEquals("payment.succeeded", inboundEvent.getEventType());
        Assertions.assertEquals("evt_12345", inboundEvent.getExternalEventId());
        Assertions.assertEquals(ProcessingStatus.RECEIVED, inboundEvent.getStatus());
        Assertions.assertNotNull(inboundEvent.getId());
        Assertions.assertNotNull(inboundEvent.getPayload());
        Assertions.assertNotNull(inboundEvent.getReceivedAt());
    }

    @Test
    public void invalidEventUnknownConnectionTest() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/connections/" + UUID.randomUUID() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {\s
                                   "externalEventId": "evt_12345",\s
                                   "eventType": "payment.succeeded",\s
                                   "payload":\s
                                   {\s
                                     "paymentId": "pay_100",\s
                                     "amount": 2500,\s
                                     "currency": "USD"\s
                                   }\s
                                 }
                                \s"""))
                .andExpect(status().isNotFound())
                .andReturn();

        ApiErrorResponse errorResponse = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                ApiErrorResponse.class);

        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals(errorResponse.status(), HttpStatus.NOT_FOUND.value());
        Assertions.assertEquals(errorResponse.error(), HttpStatus.NOT_FOUND.getReasonPhrase());
        List<InboundEvent> events = eventRepository.findAll();
        Assertions.assertEquals(0, events.size());
    }

    @Test
    public void validEventRetrivalTest() throws Exception {
        IntegrationConnection connection = new IntegrationConnection(UUID.randomUUID(),
                "test",
                "test",
                ConnectionStatus.ACTIVE,
                Instant.now());

        IntegrationConnection savedConnection = integrationConnectionRepository.save(connection);

        MvcResult result = mockMvc.perform(post("/api/v1/connections/" + savedConnection.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {\s
                                   "externalEventId": "evt_12345",\s
                                   "eventType": "payment.succeeded",\s
                                   "payload":\s
                                   {\s
                                     "paymentId": "pay_100",\s
                                     "amount": 2500,\s
                                     "currency": "USD"\s
                                   }\s
                                 }
                                \s"""))
                .andExpect(status().isCreated())
                .andReturn();
        EventResponse eventResponse = jsonMapper.readValue(result.getResponse().getContentAsString(), EventResponse.class);
        UUID eventId = eventResponse.eventId();

        MvcResult eventResult = mockMvc.perform(get("/api/v1/events/" + eventId))
                .andExpect(status().isOk())
                .andReturn();

        EventResponse getEventResponse = jsonMapper.readValue(eventResult.getResponse().getContentAsString(), EventResponse.class);
        Assertions.assertNotNull(getEventResponse);
        Assertions.assertEquals(ProcessingStatus.RECEIVED, getEventResponse.status());
    }

    @Test
    public void invalidEventRetrivalTest() throws Exception {
        MvcResult eventResult = mockMvc.perform(get("/api/v1/events/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andReturn();
        ApiErrorResponse eventResponse = jsonMapper.readValue(eventResult.getResponse().getContentAsString(), ApiErrorResponse.class);
        Assertions.assertNotNull(eventResponse);
        Assertions.assertEquals(eventResponse.status(), HttpStatus.NOT_FOUND.value());
        Assertions.assertEquals(eventResponse.error(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }
}
