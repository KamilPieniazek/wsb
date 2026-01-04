package com.example.wsb.administration.service;

import com.example.wsb.administration.model.response.AdminCancelVisitResponse;
import com.example.wsb.administration.model.response.AdminDayResponse;
import com.example.wsb.exception.BadRequestException;
import com.example.wsb.exception.NotFoundException;
import com.example.wsb.mailing.model.AdminVisitCancellationEvent;
import com.example.wsb.model.entity.*;
import com.example.wsb.repository.DayRepository;
import com.example.wsb.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminVisitService {

    private final VisitRepository visitRepository;
    private final DayRepository dayRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public List<AdminDayResponse> getSchedule(LocalDate date) {

        Day day = dayRepository.findDayByDate(date)
                .orElseThrow(() -> new NotFoundException("Day not found: " + date));

        var confirmedVisits = visitRepository.findByDayAndStatus(date, VisitStatus.CONFIRMED);

        var bySlotId = confirmedVisits.stream()
                .collect(java.util.stream.Collectors.toMap(
                        v -> v.getSlot().getId(),
                        v -> v
                ));

        return day.getSlots().stream()
                .map(slot -> {
                    var visit = bySlotId.get(slot.getId());
                    var customer = (visit != null) ? visit.getCustomer() : null;

                    return new AdminDayResponse(
                            day.getId(),
                            day.getDate(),
                            slot.getId(),
                            slot.getStartTime(),
                            slot.getStatus(),

                            visit != null ? visit.getId() : null,
                            visit != null ? visit.getVisitType() : null,
                            visit != null ? visit.getStatus() : null,

                            customer != null ? customer.getFirstName() : null,
                            customer != null ? customer.getLastName() : null,
                            customer != null ? customer.getPhoneNumber() : null,
                            customer != null ? customer.getEmail() : null
                    );
                })
                .toList();
    }

    @Transactional
    public AdminCancelVisitResponse cancelVisitAsAdmin(UUID visitId, String reason) {

        final Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new NotFoundException("Visit not found"));

        final TimeSlot slot = visit.getSlot();
        if (slot == null) {
            throw new BadRequestException("Visit has no slot assigned");
        }

        final LocalDate date = slot.getDay().getDate();
        final LocalTime time = slot.getStartTime();

        if (visit.getStatus() == VisitStatus.CANCELLED) {
            return AdminCancelVisitResponse.builder()
                    .visitId(visit.getId())
                    .date(date)
                    .time(time)
                    .visitType(visit.getVisitType())
                    .reason(reason)
                    .canceledAt(visit.getCanceledAt())
                    .build();
        }

        final String email = visit.getCustomer().getEmail();
        final Instant canceledAt = Instant.now();

        slot.setStatus(SlotStatus.AVAILABLE);
        visit.setStatus(VisitStatus.CANCELLED);
        visit.setCanceledAt(canceledAt);

        publisher.publishEvent(new AdminVisitCancellationEvent(
                visitId, email, date, time, reason
        ));

        return AdminCancelVisitResponse.builder()
                .visitId(visit.getId())
                .date(date)
                .time(time)
                .visitType(visit.getVisitType())
                .reason(reason)
                .canceledAt(canceledAt)
                .build();
    }

}

