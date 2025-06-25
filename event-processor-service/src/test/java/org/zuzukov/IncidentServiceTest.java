package org.zuzukov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.ArgumentCaptor;
import org.zuzukov.model.Event;
import org.zuzukov.model.EventTypeEnum;
import org.zuzukov.model.Incident;
import org.zuzukov.model.IncidentTypeEnum;
import org.zuzukov.repository.IncidentRepository;
import org.zuzukov.service.IncidentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Тесты для сервиса обработки инцидентов (IncidentService).
 * Проверяем создание простых и составных инцидентов,
 * а также корректную работу с репозиторием.
 */
class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        // Создаём мок репозитория, чтобы изолировать логику сервиса от базы данных
        incidentRepository = mock(IncidentRepository.class);
        incidentService = new IncidentService(incidentRepository);
    }

    /**
     * Проверяет, что при получении события TYPE_1 без соответствующего TYPE_2
     * создаётся инцидент типа SIMPLE, содержащий только это событие.
     */
    @Test
    void testSimpleIncidentWhenOnlyType1() {
        Event type1Event = new Event(UUID.randomUUID(), EventTypeEnum.TYPE_1, LocalDateTime.now());

        incidentService.handleEvent(type1Event);

        // Захватываем инцидент, который сохраняется в репозитории
        ArgumentCaptor<Incident> captor = ArgumentCaptor.forClass(Incident.class);
        verify(incidentRepository).save(captor.capture());

        Incident saved = captor.getValue();
        assertEquals(IncidentTypeEnum.SIMPLE, saved.getType(), "Должен создаться простой инцидент");
        assertEquals(1, saved.getEvents().size(), "В инциденте должно быть ровно одно событие");
        assertTrue(saved.getEvents().contains(type1Event), "Инцидент должен содержать событие TYPE_1");
    }

    /**
     * Проверяет, что при получении события TYPE_1 после ранее пришедшего TYPE_2
     * формируется составной инцидент (COMPOSITE) с двумя событиями.
     */
    @Test
    void testCompositeIncidentWhenType2BeforeType1() {
        Event type2 = new Event(UUID.randomUUID(), EventTypeEnum.TYPE_2, LocalDateTime.now().minusSeconds(5));
        Event type1 = new Event(UUID.randomUUID(), EventTypeEnum.TYPE_1, LocalDateTime.now());

        incidentService.handleEvent(type2);
        incidentService.handleEvent(type1);

        ArgumentCaptor<Incident> captor = ArgumentCaptor.forClass(Incident.class);
        verify(incidentRepository).save(captor.capture());

        Incident saved = captor.getValue();
        assertEquals(IncidentTypeEnum.COMPOSITE, saved.getType(), "Должен создаться составной инцидент");
        assertEquals(2, saved.getEvents().size(), "Инцидент должен содержать два события");
        assertTrue(saved.getEvents().contains(type1), "Инцидент должен содержать событие TYPE_1");
        assertTrue(saved.getEvents().contains(type2), "Инцидент должен содержать событие TYPE_2");
    }

    /**
     * Проверяет, что при получении только события TYPE_2
     * инциденты не создаются (потому что TYPE_2 ждёт пару TYPE_1).
     */
    @Test
    void testNoIncidentWhenOnlyType2() {
        Event type2 = new Event(UUID.randomUUID(), EventTypeEnum.TYPE_2, LocalDateTime.now());

        incidentService.handleEvent(type2);

        // Проверяем, что save на репозиторий не вызван
        verify(incidentRepository, never()).save(any());
    }

    /**
     * Проверяет корректную работу метода getAllIncidents — возвращается то, что отдаёт репозиторий.
     */
    @Test
    void testGetAllIncidents() {
        List<Incident> mockList = List.of(new Incident());
        when(incidentRepository.findAll()).thenReturn(mockList);

        List<Incident> result = incidentService.getAllIncidents();

        assertEquals(mockList, result, "Метод должен вернуть список инцидентов из репозитория");
        verify(incidentRepository).findAll();
    }
}
