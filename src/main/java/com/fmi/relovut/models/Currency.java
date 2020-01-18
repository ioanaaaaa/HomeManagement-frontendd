package com.fmi.relovut.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "currencies", uniqueConstraints = {@UniqueConstraint(columnNames = "iso_name")})
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(name = "iso_name")
    private String isoName;

    @OneToMany(mappedBy = "currency")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "fromCurrency")
    private List<ConversionRate> conversionRatesFromCurrency = new ArrayList<>();

    @OneToMany(mappedBy = "toCurrency")
    private List<ConversionRate> conversionRatesToCurrency = new ArrayList<>();

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Long version = 0L;

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isoName='" + isoName + '\'' +
                ", version=" + version +
                '}';
    }
}
