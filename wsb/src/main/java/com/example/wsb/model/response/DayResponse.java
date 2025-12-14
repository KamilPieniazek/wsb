package com.example.wsb.model.response;


import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayResponse {
    private UUID id;
    private LocalDate date;
    private boolean isFull;
    private List<SlotResponse> slots;
}
