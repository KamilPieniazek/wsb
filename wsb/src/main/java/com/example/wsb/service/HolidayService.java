package com.example.wsb.service;


import de.focus_shift.HolidayManager;
import de.focus_shift.ManagerParameters;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class HolidayService {
    private final HolidayManager holidayManager =
            HolidayManager.getInstance(ManagerParameters.create("pl"));

    public boolean isWorkingDay(LocalDate date) {
        return isWeekday(date) && !isPublicHoliday(date);
    }

    private boolean isPublicHoliday(LocalDate date) {
        return holidayManager.isHoliday(date);
    }

    private boolean isWeekday(LocalDate date) {
        return !date.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                && !date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }
}
