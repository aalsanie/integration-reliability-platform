package io.github.aalsanie.irp;

import io.github.aalsanie.irp.connections.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class ConnectionCreationIntegrationTest {

    @Autowired
    private IntegrationConnectionRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testCreatesConnectionWhenRequestIsValid() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/connections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "Stripe Production",
                              "providerType": "stripe"
                            }
                            """))
                .andExpect(status().isCreated())
                .andReturn();
        ConnectionResponse response =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ConnectionResponse.class
                );
        Assertions.assertNotNull(response.id());
        Assertions.assertEquals("Stripe Production", response.name());
        Assertions.assertEquals("STRIPE", response.providerType());
        Assertions.assertEquals(ConnectionStatus.ACTIVE, response.status());
        List<IntegrationConnection> results = repository.findAll();
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        IntegrationConnection integrationConnection = results.getFirst();
        Assertions.assertEquals("Stripe Production", integrationConnection.getName());
        Assertions.assertEquals("STRIPE", integrationConnection.getProviderType());
        Assertions.assertEquals(ConnectionStatus.ACTIVE, integrationConnection.getStatus());
        Assertions.assertNotNull(integrationConnection.getCreatedAt());
        Assertions.assertNotNull(integrationConnection.getId());
    }

    @Test
    void rejectsConnectionWhenRequiredFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/api/v1/connections")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                        {
                                          "name": "",
                                          "providerType": ""
                                        }
                                        """))
                .andExpect(status().isBadRequest());
        List<IntegrationConnection> results = repository.findAll();
        Assertions.assertEquals(0, results.size());
    }
}
