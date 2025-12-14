package com.example.wsb.repository;

import com.example.wsb.model.entity.Visit;
import com.example.wsb.model.entity.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    Optional<Visit> findByIdAndCancelToken(UUID id, UUID cancelToken);
    Optional<Visit> findByIdAndCancelTokenAndStatus(UUID id, UUID token, VisitStatus status);
}
