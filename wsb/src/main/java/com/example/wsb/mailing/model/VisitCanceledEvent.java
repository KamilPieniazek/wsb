package com.example.wsb.mailing.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record VisitCanceledEvent(
        UUID visitId,
        String email,
        LocalDate date,
        LocalTime time
) {
}
