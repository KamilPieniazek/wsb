package com.example.wsb.administration.model.response;

import com.example.wsb.model.entity.SlotStatus;
import com.example.wsb.model.entity.VisitStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AdminDayResponse(
        UUID dayId,
        LocalDate date,
        UUID slotId,
        LocalTime startTime,
        SlotStatus slotStatus,

        UUID visitId,
        String visitType,
        VisitStatus visitStatus,

        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {}
