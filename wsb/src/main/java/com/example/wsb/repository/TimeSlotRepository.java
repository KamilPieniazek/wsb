package com.example.wsb.repository;

import com.example.wsb.model.entity.TimeSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT slot FROM TimeSlot slot
        JOIN slot.day day
        WHERE day.date = :date AND slot.startTime = :time
    """)
    Optional<TimeSlot> findForUpdateByDayDateAndStartTime(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select ts from TimeSlot ts
        join ts.day d
        where d.date = :date
          and ts.startTime in :times
    """)
    List<TimeSlot> findAllForUpdateByDayDateAndStartTimeIn(
            @Param("date") LocalDate date,
            @Param("times") Collection<LocalTime> times
    );
}
