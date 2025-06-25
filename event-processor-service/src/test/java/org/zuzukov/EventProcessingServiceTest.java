package org.zuzukov;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.zuzukov.dto.EventDto;
import org.zuzukov.model.Event;
import org.zuzukov.model.EventTypeEnum;
import org.zuzukov.repository.EventRepository;
import org.zuzukov.service.EventProcessingService;
import org.zuzukov.service.IncidentService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса обработки событий (EventProcessingService).
 * Проверяем, что DTO конвертируется в Event, сохраняется в репозиторий,
 * и что событие передаётся в IncidentService.
 */
class EventProcessingServiceTest {

    private EventRepository eventRepository;
    private IncidentService incidentService;
    private EventProcessingService eventProcessingService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        incidentService = mock(IncidentService.class);
        eventProcessingService = new EventProcessingService(eventRepository, incidentService);
    }

    /**
     * Проверяем, что вызов processEvent сохраняет событие и вызывает обработчик инцидентов.
     */
    @Test
    void testProcessEvent() {
        UUID id = UUID.randomUUID();
        EventDto dto = new EventDto(id, EventTypeEnum.TYPE_1, LocalDateTime.now());

        eventProcessingService.processEvent(dto);

        // Проверяем, что EventRepository.save вызван с нужным объектом
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertEquals(dto.getId(), savedEvent.getId(), "Сохраняемый Event должен иметь тот же id, что DTO");
        assertEquals(dto.getType(), savedEvent.getType(), "Сохраняемый Event должен иметь тот же тип, что DTO");
        assertEquals(dto.getTime(), savedEvent.getTime(), "Сохраняемый Event должен иметь то же время, что DTO");

        // Проверяем, что IncidentService.handleEvent вызван с тем же Event
        verify(incidentService).handleEvent(savedEvent);
    }
}

