package com.example.bankcards.entity.spec;

import com.example.bankcards.dto.request.FilterCardBlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import org.springframework.data.jpa.domain.Specification;

public class CardBlockSpecification {

    public static Specification<CardBlockRequest> filter(FilterCardBlockRequest filter) {

        Specification<CardBlockRequest> spec = combine(null, byUserId(filter.userId()));
        spec = combine(spec, byCardId(filter.cardId()));
        spec = combine(spec, byStatus(filter.status()));

        return spec;
    }

    private static Specification<CardBlockRequest> byUserId(Long userId) {
        if (userId == null) return null;
        return (root, query, cb) ->
            cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<CardBlockRequest> byCardId(Long cardId) {
        if (cardId == null) return null;
        return (root, query, cb) ->
            cb.equal(root.get("card").get("id"), cardId);
    }

    private static Specification<CardBlockRequest> byStatus(BlockRequestStatus status) {
        if (status == null) return null;
        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }

    private static Specification<CardBlockRequest> combine(Specification<CardBlockRequest> spec1, Specification<CardBlockRequest> spec2) {
        if (spec2 == null) return spec1;
        if (spec1 == null) return spec2;
        return spec1.and(spec2);
    }
}
