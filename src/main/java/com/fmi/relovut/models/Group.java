package com.fmi.relovut.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "groupps", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private Long createdBy;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<UserGroup> userGroups = new HashSet<>();

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Group))
            return false;
        if (obj == this)
            return true;
        return this.getId().equals(((Group) obj).getId());
    }
}
