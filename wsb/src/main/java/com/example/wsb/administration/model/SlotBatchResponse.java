package com.example.wsb.administration.model;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotBatchResponse {
    private int requested;
    private int updated;
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private LocalTime time;
        private String before;
        private String after;
        private String outcome;
    }
}

