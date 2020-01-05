package com.fmi.relovut.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String fullname;

    @ManyToMany
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "friendId")
    )
    @JsonIgnoreProperties("friendOf")
    private List<User> friends = new ArrayList<>();

    @ManyToMany(mappedBy = "friends")
    @JsonIgnoreProperties("friends")
    private List<User> friendOf = new ArrayList<>();

    @OneToOne(mappedBy = "user", optional = false)
    private Account account;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Long version = 0L;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;
        return this.getId().equals(((User) obj).getId());
    }


}
