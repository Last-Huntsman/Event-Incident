package org.zuzukov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zuzukov.model.Event;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
