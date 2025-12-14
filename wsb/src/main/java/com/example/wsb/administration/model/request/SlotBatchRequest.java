package com.example.wsb.administration.model.request;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotBatchRequest {
    private LocalTime from;
    private LocalTime to;
    private List<LocalTime> times;
    private String reason;
}

