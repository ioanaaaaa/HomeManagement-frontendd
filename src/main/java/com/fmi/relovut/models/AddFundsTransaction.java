package com.fmi.relovut.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "add_funds_transactions")
public class AddFundsTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Double amount;

    @NotNull
    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Long version = 0L;
}
