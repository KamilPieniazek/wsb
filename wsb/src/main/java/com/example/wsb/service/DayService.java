package com.example.wsb.service;

import com.example.wsb.exception.ConflictException;
import com.example.wsb.exception.NotFoundException;
import com.example.wsb.model.entity.Day;
import com.example.wsb.model.entity.SlotStatus;
import com.example.wsb.model.entity.TimeSlot;
import com.example.wsb.repository.DayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayRepository dayRepository;

    @Transactional
    public List<Day> findAll() { return dayRepository.findAll(); }

    public Day getByDate(LocalDate date) {
        return dayRepository.findDayByDate(date)
                .orElseThrow(() -> new NotFoundException("Day not found"));
    }

    @Transactional
    public Day createDayWithSlots(LocalDate date, List<LocalTime> times) {

        if (dayRepository.existsByDate(date)) {
            throw new ConflictException("Day already exists for date: " + date);
        }

        final Day day = buildDay(date);
        addSlots(day, times);

        return dayRepository.save(day);
    }

    public boolean existsByDate(LocalDate date) {
        return dayRepository.existsByDate(date);
    }

    private void addSlots(Day day, List<LocalTime> times) {
        if (times == null || times.isEmpty()) {
            throw new ConflictException("Time slots list cannot be empty");
        }

        times.stream()
                .distinct()
                .sorted()
                .map(this::buildSlot)
                .forEach(day::addSlot);
    }

    private Day buildDay(LocalDate date) {
        return Day.builder()
                .date(date)
                .build();
    }

    private TimeSlot buildSlot(LocalTime time) {
        return TimeSlot.builder()
                .startTime(time)
                .status(SlotStatus.AVAILABLE)
                .build();
    }
}
