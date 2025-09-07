package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long>, JpaSpecificationExecutor<CardBlockRequest> {

    @NonNull
    Page<CardBlockRequest> findAll(@NonNull Specification<CardBlockRequest> specification, @NonNull Pageable pageable);
}
