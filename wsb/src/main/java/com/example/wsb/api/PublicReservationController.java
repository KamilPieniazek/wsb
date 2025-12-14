package com.example.wsb.api;


import com.example.wsb.model.entity.Day;
import com.example.wsb.model.request.CreateVisitRequest;
import com.example.wsb.model.response.CancelVisitResponse;
import com.example.wsb.model.response.DayResponse;
import com.example.wsb.model.response.VisitResponse;
import com.example.wsb.service.DayService;
import com.example.wsb.service.ReservationService;
import com.example.wsb.util.ReservationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicReservationController {
    private final DayService dayService;
    private final ReservationService reservationService;
    private final ReservationMapper mapper;

    @GetMapping("/days")
    public ResponseEntity<List<DayResponse>> getAllDays() {
        List<DayResponse> response = dayService.findAll().stream()
                .map(mapper::toDayResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/days/{date}")
    public ResponseEntity<DayResponse> getDay(@PathVariable final LocalDate date) {
        final Day day = dayService.getByDate(date);
        return ResponseEntity.ok(mapper.toDayResponse(day));
    }

    @PostMapping("/visits")
    public ResponseEntity<VisitResponse> book(@Valid @RequestBody final CreateVisitRequest request) {
        return ResponseEntity.status(201).body(mapper.toVisitResponse(reservationService.book(request)));
    }

    @GetMapping("/cancel/{id}")
    public ResponseEntity<CancelVisitResponse> getCancelInfo(
            @PathVariable UUID id,
            @RequestParam UUID token
    ) {
        return ResponseEntity.ok(reservationService.getCancelInfo(id, token));
    }

    @DeleteMapping("/visits/{id}")
    public ResponseEntity<CancelVisitResponse> cancelVisit(
            @PathVariable final UUID id,
            @RequestParam final UUID token
    ) {
        return ResponseEntity.ok(reservationService.cancelVisit(id, token));
    }
}
