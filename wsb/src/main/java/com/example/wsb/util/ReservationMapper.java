package com.example.wsb.util;

import com.example.wsb.model.entity.Day;
import com.example.wsb.model.entity.SlotStatus;
import com.example.wsb.model.entity.TimeSlot;
import com.example.wsb.model.entity.Visit;
import com.example.wsb.model.response.DayResponse;
import com.example.wsb.model.response.SlotResponse;
import com.example.wsb.model.response.VisitResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public DayResponse toDayResponse(Day day) {

        return DayResponse.builder()
                .id(day.getId())
                .date(day.getDate())
                .isFull(isFull(day))
                .isBlocked(day.isBlocked())
                .slots(
                        day.getSlots().stream()
                                .map(this::toSlotResponse)
                                .toList()
                )
                .build();
    }

    public SlotResponse toSlotResponse(TimeSlot slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .startTime(slot.getStartTime())
                .status(slot.getStatus())
                .build();
    }

    public VisitResponse toVisitResponse(Visit visit) {
        return VisitResponse.builder()
                .id(visit.getId())
                .date(visit.getSlot().getDay().getDate())
                .time(visit.getSlot().getStartTime())
                .firstName(visit.getCustomer().getFirstName())
                .lastName(visit.getCustomer().getLastName())
                .phoneNumber(visit.getCustomer().getPhoneNumber())
                .email(visit.getCustomer().getEmail())
                .visitType(visit.getVisitType())
                .createdAt(visit.getCreatedAt())
                .build();
    }

    private boolean isFull(Day day) {
        return day.getSlots().stream()
                .noneMatch(slot -> slot.getStatus() == SlotStatus.AVAILABLE);
    }
}
