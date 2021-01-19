package com.fmi.relovut.models;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "assignee_members")
public class AssigneeMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "assignee_id")
    private Long assigneeId;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "assignee_id", insertable = false, updatable = false)
    private Assignee assignee;

    private Long userId;
    private Long groupId;


}
