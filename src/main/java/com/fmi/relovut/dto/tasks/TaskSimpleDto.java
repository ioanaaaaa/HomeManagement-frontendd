package com.fmi.relovut.dto.tasks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fmi.relovut.models.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TaskSimpleDto {
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private Task.Category category;
}
