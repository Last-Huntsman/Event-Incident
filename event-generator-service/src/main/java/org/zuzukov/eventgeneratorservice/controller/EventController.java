package org.zuzukov.eventgeneratorservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zuzukov.eventgeneratorservice.dto.EventDto;
import org.zuzukov.eventgeneratorservice.model.EventTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final RestTemplate restTemplate;

    @Value("${processor.url}")
    private String processorUrl;

    @PostMapping("/generate")
    public void generateEvent(@RequestParam EventTypeEnum type) {
        EventDto event = new EventDto(UUID.randomUUID(), type, LocalDateTime.now());
        restTemplate.postForEntity(processorUrl, event, Void.class);
        System.out.println("Manually generated event: " + event);
    }
}
