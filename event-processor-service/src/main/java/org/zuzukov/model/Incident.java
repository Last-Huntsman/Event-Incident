package org.zuzukov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private IncidentTypeEnum type;
    private LocalDateTime time;

    @ManyToMany
    private List<Event> events;
}
