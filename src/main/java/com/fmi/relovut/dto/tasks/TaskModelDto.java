package com.fmi.relovut.dto.tasks;

import com.fmi.relovut.dto.GroupDto;
import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModelDto extends TaskSimpleDto {
    private List<UserDto> users;
    private List<GroupDto> groups;
    private Task.Status status;
    private UserDto claimedBy;

    public TaskModelDto(Task task) {
        this.setId(task.getId());
        this.setContent(task.getContent());
        this.setTitle(task.getTitle());
        this.setStatus(task.getStatus());
        this.setCategory(task.getCategory());
        if (null != task.getClaimedBy()) {
            this.setClaimedBy(new UserDto(task.getClaimedByUser(), false));
        }
    }
}
