package com.fmi.relovut.models;

import com.fmi.relovut.helpers.GeneralHelper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "id", length = 16, unique = true, nullable = false)
    private UUID id = UUID.randomUUID();

    @NotNull
    private Double amount;

    @OneToOne(optional = false, cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account")
    private Set<AddFundsTransaction> addFundsTransactions = new HashSet<>();

    @OneToMany(mappedBy = "fromAccount")
    private Set<Transaction> outgoingTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toAccount")
    private Set<Transaction> incomingTransactions = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Long version = 0L;

    public Account setAmount(Double amount) {
        this.amount = GeneralHelper.round(amount, 4);
        return this;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Account))
            return false;
        if (obj == this)
            return true;
        return this.getId().equals(((Account) obj).getId());
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                ", user=" + user.getId() +
                ", currency=" + currency +
                ", version=" + version +
                '}';
    }
}
