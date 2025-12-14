package com.example.wsb.administration.api;

import com.example.wsb.administration.model.request.AdminCancelVisitRequest;
import com.example.wsb.administration.model.response.AdminCancelVisitResponse;
import com.example.wsb.administration.model.response.AdminDayResponse;
import com.example.wsb.administration.service.AdminVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final AdminVisitService visitService;

    @GetMapping("/{date}/visits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminDayResponse>> getReservedVisitsForDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(visitService.getReservedVisitsForDay(date));
    }

    @PatchMapping("/{visitId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCancelVisitResponse> cancel(
            @PathVariable UUID visitId,
            @RequestBody AdminCancelVisitRequest request
    ) {
        return ResponseEntity.ok(visitService.cancelVisitAsAdmin(visitId, request.reason()));
    }
}
