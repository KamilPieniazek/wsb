package com.example.wsb.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "visit",
        uniqueConstraints = @UniqueConstraint(name = "uk_visit_slot", columnNames = "slot_id"),
        indexes = @Index(name = "ix_visit_created_at", columnList = "created_at"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private TimeSlot slot;

    @Embedded
    private CustomerDetails customer;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "visit_type", length = 255)
    private String visitType;

    @Column(name = "cancel_token", nullable = false, unique = true, updatable = false)
    private UUID cancelToken;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VisitStatus status = VisitStatus.CONFIRMED;

    @Column(name = "canceled_at")
    private Instant canceledAt;

    @PrePersist
    void initToken() {
        if (cancelToken == null) {
            cancelToken = UUID.randomUUID();
        }
        if (status == null) {
            status = VisitStatus.CONFIRMED;
        }
    }
}
