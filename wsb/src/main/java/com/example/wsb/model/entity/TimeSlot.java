package com.example.wsb.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "time_slot",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_slot_day_time",
                columnNames = {"day_id", "start_time"}
        ),
        indexes = {
                @Index(name = "ix_slot_day", columnList = "day_id"),
                @Index(name = "ix_slot_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "day_id", nullable = false)
    private Day day;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @OneToMany(mappedBy = "slot", fetch = FetchType.LAZY)
    private List<Visit> visits = new ArrayList<>();

    public boolean hasActiveVisit() {
        return visits.stream().anyMatch(v -> v.getStatus() == VisitStatus.CONFIRMED);
    }
}
