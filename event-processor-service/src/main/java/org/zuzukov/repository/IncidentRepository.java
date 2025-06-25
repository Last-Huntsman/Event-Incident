package org.zuzukov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zuzukov.model.Incident;

import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
}
