package com.example.wsb.util;

import com.example.wsb.service.DayService;
import com.example.wsb.service.HolidayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DayCreationScheduler {

    private final DayService dayService;
    private final HolidayService holidayService;

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");

    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(16, 0);

    private static final LocalTime BREAK_START = LocalTime.of(12, 0);
    private static final LocalTime BREAK_END = LocalTime.of(13, 0);

    private static final int SLOT_MINUTES = 30;
    @Scheduled(cron = "0 2 0 * * *", zone = "Europe/Warsaw")
    @Transactional
    public void createSchedule() {
        LocalDate today = LocalDate.now(ZONE);

        List<LocalTime> slots = generateSlots();

        for (int i = 0; i <= 7; i++) {
            LocalDate date = today.plusDays(i);

            if (shouldSkipDate(date)) {
                continue;
            }

            dayService.createDayWithSlots(date, slots);
        }
    }

    private List<LocalTime> generateSlots() {
        List<LocalTime> result = new ArrayList<>();

        for (LocalTime t = START_TIME; t.isBefore(END_TIME); t = t.plusMinutes(SLOT_MINUTES)) {
            if (!t.isBefore(BREAK_START) && t.isBefore(BREAK_END)) {
                continue;
            }

            result.add(t);
        }

        return result;
    }

    private boolean shouldSkipDate(LocalDate date) {
        return !holidayService.isWorkingDay(date)
                || dayService.existsByDate(date);
    }
}
