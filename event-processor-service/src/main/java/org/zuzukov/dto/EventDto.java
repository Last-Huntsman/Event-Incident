package org.zuzukov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zuzukov.model.EventTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class EventDto {
    private UUID id;
    private EventTypeEnum type;
    private LocalDateTime time;
}


