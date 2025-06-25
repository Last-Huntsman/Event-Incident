package org.zuzukov.eventgeneratorservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.zuzukov.eventgeneratorservice.dto.EventDto;
import org.zuzukov.eventgeneratorservice.model.EventTypeEnum;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
/**
 * Этот класс автоматически создает случайные события и отправляет их на сервер.
 * Как только приложение запускается, он начинает в отдельном потоке постоянно создавать
 * новые события с небольшими паузами и отправлять их по нужному адресу.

 */
@Component
@RequiredArgsConstructor
public class EventGenerator {

    private final RestTemplate restTemplate;
    private final Random random = new Random();

    @Value("${processor.url}")
    private String processorUrl;
    @PostConstruct
    public void start() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                generateAndSend();
                try {
                    Thread.sleep(random.nextInt(2000)); // от 0 до 2 сек
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, Failed to complete operation");
                }
            }
        });
    }

    private void generateAndSend() {
        EventDto event = new EventDto(
                UUID.randomUUID(),
                EventTypeEnum.values()[random.nextInt(4)],
                LocalDateTime.now()
        );
        try {
            restTemplate.postForEntity(processorUrl, event, Void.class);
            System.out.println("Auto generated event: " + event);
        } catch (Exception e) {
            System.out.println("Failed to send event: " + e.getMessage());
        }
    }
}