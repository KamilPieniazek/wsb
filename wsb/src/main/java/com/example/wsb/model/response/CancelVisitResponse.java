package com.example.wsb.model.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class CancelVisitResponse {
    private final UUID visitId;
    private final LocalDate date;
    private final LocalTime time;
    private final String visitType;
}
