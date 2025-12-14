package com.example.wsb.administration.service;

import com.example.wsb.administration.model.response.AdminCancelVisitResponse;
import com.example.wsb.administration.model.response.AdminDayResponse;
import com.example.wsb.exception.BadRequestException;
import com.example.wsb.exception.NotFoundException;
import com.example.wsb.mailing.model.AdminVisitCancellationEvent;
import com.example.wsb.model.entity.SlotStatus;
import com.example.wsb.model.entity.TimeSlot;
import com.example.wsb.model.entity.Visit;
import com.example.wsb.model.entity.VisitStatus;
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
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public List<AdminDayResponse> getReservedVisitsForDay(LocalDate date) {
        return visitRepository.findByDayAndStatus(date, VisitStatus.CONFIRMED)
                .stream()
                .map(visit -> {
                    var slot = visit.getSlot();
                    var day = slot.getDay();
                    var customer = visit.getCustomer();

                    return new AdminDayResponse(
                            visit.getId(),
                            slot.getId(),
                            day.getDate(),
                            slot.getStartTime(),
                            visit.getVisitType(),
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

