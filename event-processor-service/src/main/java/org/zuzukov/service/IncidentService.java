package org.zuzukov.service;

import org.springframework.stereotype.Service;
import org.zuzukov.model.Event;
import org.zuzukov.model.EventTypeEnum;
import org.zuzukov.model.Incident;
import org.zuzukov.model.IncidentTypeEnum;
import org.zuzukov.repository.IncidentRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Этот сервис обрабатывает события и создаёт инциденты.
 *
 * - Если пришло событие типа 1, он ищет похожее событие типа 2, которое было недавно.
 *   Если находит — делает один общий инцидент из двух событий.
 *   Если нет — делает инцидент только с событием типа 1.
 * - Если пришло событие типа 2, просто сохраняет его временно.
 * - Удаляет старые события типа 2, которым больше 20 секунд.
 * - Можно получить список всех инцидентов.
 */

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;


    private final Map<UUID, Event> bufferedType2Events = new HashMap<>();

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public void handleEvent(Event event) {
        if (event.getType() == EventTypeEnum.TYPE_1) {
            processType1Event(event);
        } else if (event.getType() == EventTypeEnum.TYPE_2) {
            bufferType2Event(event);
        }

        // Удаляем просроченные TYPE_2 события
        cleanupExpiredType2Events();
    }


    private void processType1Event(Event type1Event) {
        Optional<Map.Entry<UUID, Event>> matched = bufferedType2Events.entrySet().stream()
                .filter(entry -> isWithinThreshold(entry.getValue(), type1Event))
                .findFirst();

        if (matched.isPresent()) {
            Event type2 = matched.get().getValue();
            Incident compositeIncident = new Incident(
                    UUID.randomUUID(),
                    IncidentTypeEnum.COMPOSITE,
                    LocalDateTime.now(),
                    List.of(type2, type1Event)
            );
            incidentRepository.save(compositeIncident);
            bufferedType2Events.remove(matched.get().getKey());
        } else {
            Incident simpleIncident = new Incident(
                    UUID.randomUUID(),
                    IncidentTypeEnum.SIMPLE,
                    LocalDateTime.now(),
                    List.of(type1Event)
            );
            incidentRepository.save(simpleIncident);
        }
    }


    private void bufferType2Event(Event type2Event) {
        bufferedType2Events.put(type2Event.getId(), type2Event);
    }


    private void cleanupExpiredType2Events() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(20);
        bufferedType2Events.entrySet().removeIf(e -> e.getValue().getTime().isBefore(threshold));
    }


    private boolean isWithinThreshold(Event earlier, Event later) {
        return Duration.between(earlier.getTime(), later.getTime()).getSeconds() <= 20;
    }


    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
}
