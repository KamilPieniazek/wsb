package com.example.wsb.administration.service;

import com.example.wsb.administration.model.request.SlotBatchRequest;
import com.example.wsb.administration.model.response.SlotBatchResponse;
import com.example.wsb.exception.NotFoundException;
import com.example.wsb.model.entity.Day;
import com.example.wsb.model.entity.SlotStatus;
import com.example.wsb.model.entity.TimeSlot;
import com.example.wsb.repository.DayRepository;
import com.example.wsb.repository.TimeSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminScheduleService {

    private final DayRepository dayRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public void closeDay(LocalDate date) {
        Day day = dayRepository.findByDateForUpdate(date)
                .orElseThrow(() -> new NotFoundException("Day not found: " + date));

        day.setBlocked(true);

        for (TimeSlot slot : day.getSlots()) {
            if (slot.getStatus() == SlotStatus.AVAILABLE) {
                slot.setStatus(SlotStatus.BLOCKED);
            }
        }
    }

    @Transactional
    public void openDay(LocalDate date) {
        Day day = dayRepository.findByDateForUpdate(date)
                .orElseThrow(() -> new NotFoundException("Day not found: " + date));

        day.setBlocked(false);

        for (TimeSlot slot : day.getSlots()) {
            if (slot.getStatus() == SlotStatus.BLOCKED) {
                slot.setStatus(SlotStatus.AVAILABLE);
            }
        }
    }

    @Transactional
    public SlotBatchResponse blockSlots(LocalDate date, SlotBatchRequest request) {
        return updateSlots(date, request, SlotStatus.BLOCKED);
    }

    @Transactional
    public SlotBatchResponse unblockSlots(LocalDate date, SlotBatchRequest request) {
        return updateSlots(date, request, SlotStatus.AVAILABLE);
    }

    private SlotBatchResponse updateSlots(LocalDate date, SlotBatchRequest request, SlotStatus target) {
        List<LocalTime> times = resolveTimes(request);
        if (times.isEmpty()) {
            return SlotBatchResponse.builder()
                    .requested(0).updated(0).items(List.of())
                    .build();
        }


        List<TimeSlot> slots = timeSlotRepository.findAllForUpdateByDayDateAndStartTimeIn(date, times);

        Map<LocalTime, TimeSlot> byTime = slots.stream()
                .collect(Collectors.toMap(TimeSlot::getStartTime, s -> s));

        List<SlotBatchResponse.Item> items = new ArrayList<>();
        int updated = 0;

        for (LocalTime t : times) {
            TimeSlot slot = byTime.get(t);
            if (slot == null) {
                items.add(item(t, null, null, "NOT_FOUND"));
                continue;
            }

            SlotStatus before = slot.getStatus();

            if (slot.hasActiveVisit() || before == SlotStatus.BOOKED) {
                items.add(item(t, before.name(), before.name(), "SKIPPED_BOOKED"));
                continue;
            }

            if (before == target) {
                items.add(item(t, before.name(), before.name(), "NO_CHANGE"));
                continue;
            }

            slot.setStatus(target);
            updated++;
            items.add(item(t, before.name(), target.name(), "UPDATED"));
        }

        return SlotBatchResponse.builder()
                .requested(times.size())
                .updated(updated)
                .items(items)
                .build();
    }

    private static SlotBatchResponse.Item item(LocalTime time, String before, String after, String outcome) {
        return SlotBatchResponse.Item.builder()
                .time(time).before(before).after(after).outcome(outcome)
                .build();
    }

    private List<LocalTime> resolveTimes(SlotBatchRequest request) {
        if (request.getTimes() != null && !request.getTimes().isEmpty()) {
            return request.getTimes().stream().distinct().sorted().toList();
        }

        LocalTime from = request.getFrom();
        LocalTime to = request.getTo();
        if (from == null || to == null) return List.of();
        if (to.isBefore(from)) return List.of();

        List<LocalTime> response = new ArrayList<>();
        for (LocalTime cur = from; !cur.isAfter(to); cur = cur.plusMinutes(30)) {
            response.add(cur);
        }
        return response;
    }
}

