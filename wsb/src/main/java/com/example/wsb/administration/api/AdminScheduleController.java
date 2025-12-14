package com.example.wsb.administration.api;

import com.example.wsb.administration.model.SlotBatchRequest;
import com.example.wsb.administration.model.SlotBatchResponse;
import com.example.wsb.administration.service.AdminScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final AdminScheduleService service;

    @PostMapping("/days/{date}/close")
    public ResponseEntity<Void> closeDay(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        service.closeDay(date);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/days/{date}/open")
    public ResponseEntity<Void> openDay(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        service.openDay(date);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/days/{date}/slots/block")
    public ResponseEntity<SlotBatchResponse> block(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody SlotBatchRequest request
    ) {
        return ResponseEntity.ok(service.blockSlots(date, request));
    }

    @PostMapping("/days/{date}/slots/unblock")
    public ResponseEntity<SlotBatchResponse> unblock(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody SlotBatchRequest request
    ) {
        return ResponseEntity.ok(service.unblockSlots(date, request));
    }
}

