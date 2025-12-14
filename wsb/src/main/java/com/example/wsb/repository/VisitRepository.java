package com.example.wsb.repository;

import com.example.wsb.model.entity.Visit;
import com.example.wsb.model.entity.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    Optional<Visit> findByIdAndCancelToken(UUID id, UUID cancelToken);

    @Query("""
        SELECT visit
        FROM Visit visit
        JOIN FETCH visit.slot slot
        JOIN FETCH slot.day day
        WHERE day.date = :date
          AND visit.status = :status
          AND visit.slot IS NOT NULL
        ORDER BY slot.startTime ASC
    """)
    List<Visit> findByDayAndStatus(@Param("date") LocalDate date,
                                   @Param("status") VisitStatus status);
}
