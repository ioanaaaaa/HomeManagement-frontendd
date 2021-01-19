package com.fmi.relovut.models;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "tasks")
public class Task {
    public enum Status{
        IN_PROGRESS, COMPLETED, CREATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne()
    @JoinColumn(name = "taskCategory_id")
    private TaskCategory taskCategory;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = true)
    private Set<Assignee> assignedUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = true)
    @Where(clause = "\"ACTIVE\"=true")
    private Set<Assignee> activeAssignedUsers = new HashSet<>();
}
