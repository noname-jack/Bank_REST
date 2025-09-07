package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;
    @ManyToOne
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status = TransferStatus.PENDING;

    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
