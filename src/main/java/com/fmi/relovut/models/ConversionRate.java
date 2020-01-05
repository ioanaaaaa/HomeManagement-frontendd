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
@Table(name = "conversion_rates")
public class ConversionRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Double rate;

    @NotNull
    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_currency_id")
    private Currency fromCurrency;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_currency_id")
    private Currency toCurrency;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Long version = 0L;
}
