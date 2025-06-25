package org.zuzukov.eventgeneratorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zuzukov.eventgeneratorservice.model.EventTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для передачи данных события")
public class EventDto {
    @Schema(description = "Уникальный идентификатор события", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Тип события", example = "TYPE_1")
    private EventTypeEnum type;

    @Schema(description = "Время генерации события", example = "2023-10-01T12:00:00")
    private LocalDateTime time;
}