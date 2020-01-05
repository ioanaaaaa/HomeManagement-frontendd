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
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Double amount;

    @NotNull
    private Double rate;

    @NotNull
    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Long version = 0L;
}
