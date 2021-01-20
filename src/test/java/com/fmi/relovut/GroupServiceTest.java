package com.fmi.relovut;

import com.fmi.relovut.dto.GroupDto;
import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.Group;
import com.fmi.relovut.models.UserGroup;
import com.fmi.relovut.repositories.GroupRepository;
import com.fmi.relovut.repositories.UserGroupRepository;
import com.fmi.relovut.repositories.UserRepository;
import com.fmi.relovut.services.GroupService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GroupServiceTest {
    @InjectMocks
    private GroupService groupService;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Set<UserGroup>> userGroupEntityArgumentCaptor;

    @Captor
    private ArgumentCaptor<Set<UserGroup>> saveUserGroupEntityArgumentCaptor;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        groupService = new GroupService(groupRepository, userGroupRepository, userRepository);
    }

    @Test
    public void remove_all_members(){
        Group group = buildGroup();
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        GroupDto groupDto = GroupDto.builder()
                .userDtoSet(null)
                .id(1L)
                .name("Name")
                .build();
        groupService.createOrUpdateGroup(groupDto, null);

        Assert.assertEquals(new HashSet<>(), group.getUserGroups());
    }

    @Test
    public void replace_member(){
        Group group = buildGroup();
        Set<UserGroup> oldUserGroup = group.getUserGroups();
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        GroupDto groupDto = GroupDto.builder()
                .userDtoSet(Set.of(buildUserDto(false, 5L)))
                .id(1L)
                .name("Name")
                .build();
        groupService.createOrUpdateGroup(groupDto, null);

        Mockito.verify(this.userGroupRepository,
                Mockito.times(1)).deleteAll(userGroupEntityArgumentCaptor.capture());
        Set<UserGroup> userGroupDeleted = userGroupEntityArgumentCaptor.getValue();

        Mockito.verify(this.userGroupRepository,
                Mockito.times(1)).saveAll(saveUserGroupEntityArgumentCaptor.capture());
        Set<UserGroup> userGroupAdded = saveUserGroupEntityArgumentCaptor.getValue();

        Assert.assertEquals(1, userGroupDeleted.size());
        Assert.assertEquals(oldUserGroup, userGroupDeleted);
        Assert.assertEquals(Long.valueOf(5), userGroupAdded.stream().findAny().get().getUserId());
    }

    @Test
    public void add_member(){
        Group group = new Group();

        when(groupRepository.save(any())).thenReturn(group);

        GroupDto groupDto = GroupDto.builder()
                .userDtoSet(Set.of(buildUserDto(false, 5L)))
                .name("Name")
                .build();
        groupService.createOrUpdateGroup(groupDto, null);

        Mockito.verify(this.userGroupRepository,
                Mockito.times(1)).saveAll(saveUserGroupEntityArgumentCaptor.capture());
        Set<UserGroup> userGroupAdded = saveUserGroupEntityArgumentCaptor.getValue();

        Assert.assertEquals(1, userGroupAdded.size());
    }

    @Test
    public void update_manager(){
        Group group = buildGroup();
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        GroupDto groupDto = GroupDto.builder()
                .id(1L)
                .userDtoSet(Set.of(buildUserDto(false, 5L)))
                .name("Name")
                .build();
        groupService.createOrUpdateGroup(groupDto, null);

        Mockito.verify(this.userGroupRepository,
                Mockito.times(1)).saveAll(saveUserGroupEntityArgumentCaptor.capture());
        UserGroup userGroup = saveUserGroupEntityArgumentCaptor.getValue().iterator().next();

        Assert.assertEquals(false, userGroup.isManager());
    }

    public Group buildGroup(){
        Set<UserGroup> userGroupSet = new HashSet<>();
        userGroupSet.add(buildUserGroup());
        return  Group.builder()
                .id(1L)
                .name("group1")
                .userGroups(userGroupSet)
                .build();
    }

    public UserGroup buildUserGroup(){
        return UserGroup.builder()
                .userId(Long.valueOf("2"))
                .groupId(Long.valueOf(1))
                .isManager(true)
                .build();
    }

    public UserDto buildUserDto(boolean isManager, Long id){
        return UserDto.userDtoBuilder()
                .isManager(isManager)
                .id(id)
                .build();
    }
}
