package org.zuzukov;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.zuzukov.controller.EventController;
import org.zuzukov.dto.EventDto;
import org.zuzukov.model.Incident;
import org.zuzukov.model.IncidentTypeEnum;
import org.zuzukov.service.EventProcessingService;
import org.zuzukov.service.IncidentService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private MockMvc mockMvc;
    private EventProcessingService eventProcessingService;
    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        eventProcessingService = mock(EventProcessingService.class);
        incidentService = mock(IncidentService.class);
        EventController controller = new EventController(eventProcessingService, incidentService);

        //  Оборачиваем кастомный ObjectMapper в конвертер
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        //  Добавляем конвертер в MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(converter)
                .build();
    }


    @Test
    void testAcceptEvent() throws Exception {
        EventDto dto = new EventDto(UUID.randomUUID(), null, LocalDateTime.now());
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(eventProcessingService).processEvent(any(EventDto.class));
    }

    @Test
    void testFetchAllIncidents() throws Exception {
        Incident incident = new Incident(UUID.randomUUID(), IncidentTypeEnum.SIMPLE, LocalDateTime.now(), List.of());
        List<Incident> incidentList = List.of(incident);

        when(incidentService.getAllIncidents()).thenReturn(incidentList);

        mockMvc.perform(get("/api/events/incidents"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(incidentList)));

        verify(incidentService).getAllIncidents();
    }
}
