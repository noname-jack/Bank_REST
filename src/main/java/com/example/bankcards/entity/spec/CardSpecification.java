package com.example.bankcards.entity.spec;

import com.example.bankcards.dto.request.FilterCardAdminRequest;
import com.example.bankcards.dto.request.FilterCardUserRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CardSpecification {
    public static Specification<Card> hasId(Long id) {
        if (id == null) return null;
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }


    public static Specification<Card> hasOwnerId(Long ownerId) {
        if (ownerId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), ownerId);
    }

    public static Specification<Card> ownerUserNameLike(String username) {
        if (username == null || username.isEmpty()) return null;
        return (root, query, cb) -> cb.like(root.get("user").get("username"), "%" + username + "%");
    }

    public static Specification<Card> hasStatus(CardStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Card> balanceMin(BigDecimal min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("balance"), min);
    }

    public static Specification<Card> balanceMax(BigDecimal max) {
        if (max == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("balance"), max);
    }

    public static Specification<Card> filter(FilterCardAdminRequest filter) {

        Specification<Card> spec = combine(null, hasId(filter.id()));

        spec = combine(spec, hasOwnerId(filter.ownerId()));
        spec = combine(spec, ownerUserNameLike(filter.ownerUserName()));
        spec = combine(spec, hasStatus(filter.cardStatus()));
        spec = combine(spec, balanceMin(filter.balanceMin()));
        spec = combine(spec, balanceMax(filter.balanceMax()));

        return spec;
    }

    public static Specification<Card> filterByUserId(FilterCardUserRequest filter, Long userId) {

        Specification<Card> spec = combine(null, hasOwnerId(userId));

        spec = combine(spec, hasId(filter.id()));
        spec = combine(spec, hasStatus(filter.cardStatus()));
        spec = combine(spec, balanceMin(filter.balanceMin()));
        spec = combine(spec, balanceMax(filter.balanceMax()));

        return spec;
    }

    private static Specification<Card> combine(Specification<Card> spec1, Specification<Card> spec2) {
        if (spec2 == null) return spec1;
        if (spec1 == null) return spec2;
        return spec1.and(spec2);
    }
}
