package org.zuzukov.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zuzukov.dto.EventDto;
import org.zuzukov.model.Incident;
import org.zuzukov.service.EventProcessingService;
import org.zuzukov.service.IncidentService;

import java.util.List;

// Класс — контроллер, который принимает события от генератора

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {

    private final EventProcessingService eventProcessor;
    private final IncidentService incidentTracker;



    @PostMapping
    public ResponseEntity<Void> acceptEvent(@RequestBody EventDto payload) {
        eventProcessor.processEvent(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> fetchAllIncidents() {
        List<Incident> incidents = incidentTracker.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }
}
