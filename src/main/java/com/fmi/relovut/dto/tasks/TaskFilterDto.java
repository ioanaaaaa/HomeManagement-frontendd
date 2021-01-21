package com.fmi.relovut.dto.tasks;

import com.fmi.relovut.models.Task;
import lombok.Data;

@Data
public class TaskFilterDto extends IsMatchDto {
    private Task.Status status;
    private Long groupId;
    private Long userId;


}
