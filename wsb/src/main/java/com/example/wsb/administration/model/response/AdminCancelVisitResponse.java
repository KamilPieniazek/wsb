package com.example.wsb.administration.model.response;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record AdminCancelVisitResponse(
        UUID visitId,
        LocalDate date,
        LocalTime time,
        String visitType,
        String reason,
        Instant canceledAt
) {}
