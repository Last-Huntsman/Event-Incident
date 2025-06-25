package org.zuzukov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zuzukov.dto.EventDto;
import org.zuzukov.model.Event;
import org.zuzukov.repository.EventRepository;

// Сервис для обработки входящих событий.
// Получает данные события, сохраняет их в базу,
// а потом передает событие сервису, который отслеживает инциденты.
@RequiredArgsConstructor
@Service
public class EventProcessingService {

    private final EventRepository eventRepository;
    private final IncidentService incidentService;


    public void processEvent(EventDto dto) {

        Event event = new Event(dto.getId(), dto.getType(), dto.getTime());


        eventRepository.save(event);


        incidentService.handleEvent(event);
    }
}
