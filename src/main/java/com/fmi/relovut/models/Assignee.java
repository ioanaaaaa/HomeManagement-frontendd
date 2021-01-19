package com.fmi.relovut.models;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "assignees")
public class Assignee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "task_id")
    private Long taskId;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private Task task;


}
