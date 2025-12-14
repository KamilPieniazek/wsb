package com.example.wsb.model.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponse {
    private UUID id;

    private LocalDate date;
    private LocalTime time;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    private String visitType;
    private Instant createdAt;
}
