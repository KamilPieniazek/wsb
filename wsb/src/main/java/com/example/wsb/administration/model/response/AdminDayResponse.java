package com.example.wsb.administration.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AdminDayResponse(
        UUID visitId,
        UUID slotId,
        LocalDate date,
        LocalTime time,
        String visitType,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {}
