package com.fmi.relovut.dto;

import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.Group;
import com.fmi.relovut.models.User;
import com.fmi.relovut.models.UserGroup;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Data
@Builder
public class GroupDto {
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Set<UserDto> userDtoSet;

    public static final GroupDto toDto(Group group){
        Map<User, Boolean> users = group.getUserGroups().stream().collect(toMap(obj -> obj.getUser(), userGroup -> userGroup.isManager()));
        return GroupDto.builder()
                .name(group.getName())
                .id(group.getId())
                .userDtoSet(UserDto.toDtos(users))
                .build();
    }

    public static final Set<GroupDto> toDtos(List<Group> groupList){
        return  groupList.stream().map(GroupDto::toDto).collect(Collectors.toSet());
    }
}
