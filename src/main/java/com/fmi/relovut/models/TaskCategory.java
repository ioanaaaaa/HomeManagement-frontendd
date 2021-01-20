//package com.fmi.relovut.models;
//
//import lombok.Data;
//import lombok.experimental.Accessors;
//
//import javax.persistence.*;
//import java.util.HashSet;
//import java.util.Set;
//
//@Data
//@Accessors(chain = true)
//@Entity
//@Table(name = "task_categories")
//public class TaskCategory {
//    public enum Category{
//        Shopping, Cleaning
//    }
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    private Category name;
//
//    @OneToMany(mappedBy = "taskCategory")
//    private Set<Task> taskSet = new HashSet<>();
//}
