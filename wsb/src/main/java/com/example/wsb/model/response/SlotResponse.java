package com.example.wsb.model.response;

import com.example.wsb.model.entity.SlotStatus;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotResponse {
    private UUID id;
    private LocalTime startTime;
    private SlotStatus status;
}
