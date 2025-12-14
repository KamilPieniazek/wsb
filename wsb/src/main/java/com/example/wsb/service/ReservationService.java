package com.example.wsb.service;

import com.example.wsb.exception.ConflictException;
import com.example.wsb.exception.NotFoundException;
import com.example.wsb.model.entity.*;
import com.example.wsb.model.request.CreateVisitRequest;
import com.example.wsb.model.response.CancelVisitResponse;
import com.example.wsb.repository.TimeSlotRepository;
import com.example.wsb.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.wsb.mailing.model.VisitBookedEvent;
import com.example.wsb.mailing.model.VisitCanceledEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final TimeSlotRepository timeSlotRepository;
    private final VisitRepository visitRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Visit book(CreateVisitRequest request) {

        final Visit visit = Optional.of(request)
                .map(this::findAvailableSlot)
                .map(slot -> buildVisit(
                        slot,
                        buildCustomerDetails(request),
                        request
                ))
                .map(this::attachAndBook)
                .map(visitRepository::save)
                .orElseThrow();

        publisher.publishEvent(new VisitBookedEvent(
                visit.getId(),
                visit.getCustomer().getEmail(),
                visit.getSlot().getDay().getDate(),
                visit.getSlot().getStartTime(),
                visit.getCancelToken()
        ));

        return visit;
    }

    @Transactional
    public CancelVisitResponse getCancelInfo(UUID visitId, UUID token) {

        Visit visit = visitRepository.findByIdAndCancelToken(visitId, token)
                .orElseThrow(() -> new NotFoundException("Visit not found or invalid token"));

        TimeSlot slot = visit.getSlot();

        return CancelVisitResponse.builder()
                .visitId(visit.getId())
                .date(slot != null ? slot.getDay().getDate() : null)
                .time(slot != null ? slot.getStartTime() : null)
                .visitType(visit.getVisitType())
                .build();
    }

    @Transactional
    public CancelVisitResponse cancelVisit(UUID visitId, UUID token) {
        final Visit visit = visitRepository
                .findByIdAndCancelToken(visitId, token)
                .orElseThrow(() -> new NotFoundException("Visit not found or invalid token"));

        final TimeSlot slot = visit.getSlot();
        if (visit.getStatus() == VisitStatus.CANCELLED) {
            return CancelVisitResponse.builder()
                    .visitId(visit.getId())
                    .date(slot.getDay().getDate())
                    .time(slot.getStartTime())
                    .visitType(visit.getVisitType())
                    .build();
        }

        final String email = visit.getCustomer().getEmail();
        final LocalDate date = slot.getDay().getDate();
        final LocalTime time = slot.getStartTime();

        slot.setStatus(SlotStatus.AVAILABLE);
        visit.setStatus(VisitStatus.CANCELLED);
        visit.setCanceledAt(Instant.now());;


        publisher.publishEvent(new VisitCanceledEvent(visitId, email, date, time));

        return CancelVisitResponse.builder()
                .visitId(visit.getId())
                .date(date)
                .time(time)
                .visitType(visit.getVisitType())
                .build();
    }

    private TimeSlot findAvailableSlot(CreateVisitRequest request) {
        TimeSlot slot = timeSlotRepository
                .findForUpdateByDayDateAndStartTime(request.getDate(), request.getTime())
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Time slot for %s %s not found",
                        request.getDate(),
                        request.getTime()
                )));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ConflictException(String.format(
                    "Time slot for %s %s is not available",
                    request.getDate(),
                    request.getTime()
            ));
        }

        if (slot.hasActiveVisit()) {
            throw new ConflictException("Time slot has a confirmed visit");
        }

        return slot;
    }

    private Visit attachAndBook(Visit visit) {
        final TimeSlot slot = visit.getSlot();

        slot.setStatus(SlotStatus.BOOKED);
        slot.getVisits().add(visit);
        visit.setSlot(slot);

        return visit;
    }

    private CustomerDetails buildCustomerDetails(final CreateVisitRequest request) {
        return CustomerDetails.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
    }

    private Visit buildVisit(
            final TimeSlot slot,
            final CustomerDetails customerDetails,
            final CreateVisitRequest request) {

        return Visit.builder()
                .slot(slot)
                .customer(customerDetails)
                .visitType(request.getVisitType())
                .build();
    }
}
