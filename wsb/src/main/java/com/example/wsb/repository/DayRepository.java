package com.example.wsb.repository;

import com.example.wsb.model.entity.Day;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DayRepository extends JpaRepository<Day, UUID> {
    @EntityGraph(attributePaths = "slots")
    List<Day> findAllByDateGreaterThanEqualOrderByDateAsc(LocalDate localDate);

    @EntityGraph(attributePaths = "slots")
    Optional<Day> findDayByDate(LocalDate date);

    boolean existsByDate(LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT day FROM Day day LEFT JOIN FETCH day.slots WHERE day.date = :date")
    Optional<Day> findByDateForUpdate(@Param("date") LocalDate date);
}
