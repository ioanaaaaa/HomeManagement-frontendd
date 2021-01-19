package com.fmi.relovut.services;

import com.fmi.relovut.dto.GroupDto;
import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.Group;
import com.fmi.relovut.models.UserGroup;
import com.fmi.relovut.repositories.GroupRepository;
import com.fmi.relovut.repositories.UserGroupRepository;
import com.fmi.relovut.repositories.UserRepository;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserGroupRepository userGroupRepository, UserRepository userRepository){
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Group> getGroups(){
         return groupRepository.findAll();
    }

    @Transactional
    public void deleteGroupById(Long groupId){
        groupRepository.deleteById(groupId);
    }

    @SneakyThrows
    @Transactional
    public void createOrUpdateGroup(GroupDto groupDto){
        Long id = groupDto.getId();
        Group group;
        if(null == id){//insert
            group = Group.builder()
                    .name(groupDto.getName())
                    .build();
            group = groupRepository.save(group);
        } else {// update
            Optional<Group> groupOptional = groupRepository.findById(id);
            if(groupOptional.isPresent()){
                group = groupOptional.get().setName(groupDto.getName());
                groupRepository.save(group);
            } else {
                throw new NotFoundException("Group with id:" + id + " does not exist");
            }
        }

        Map<Long, UserGroup> existingUsers = new HashMap<>();
        if(!CollectionUtils.isEmpty(group.getUserGroups())) {
            existingUsers = group.getUserGroups().stream()
                    .collect(toMap(UserGroup::getUserId, userGroup -> userGroup));
        }

        Map<Long, UserDto> newUsers = new HashMap<>();
        if(!CollectionUtils.isEmpty(groupDto.getUserDtoSet())) {
            newUsers = groupDto.getUserDtoSet().stream()
                    .collect(toMap(UserDto::getId, userDto -> userDto));
        }

        if(CollectionUtils.isEmpty(newUsers)){//remove all users from groupOptional
            group.setUserGroups(new HashSet<>());//not sure if this deletes all userGroup
            userGroupRepository.deleteByGroupId(group.getId());
            groupRepository.save(group);
        } else {
            updateUserGroupLinks(group.getId(), existingUsers, newUsers);
        }
    }

    public void updateUserGroupLinks(Long groupId, Map<Long, UserGroup> existingUsers, Map<Long, UserDto> newUsers){
        Set<UserGroup> userGroupsToRemove = new HashSet<>();
        Set<UserGroup> userGroupsToUpdate = new HashSet<>();

        Set<Long> newUserIds = newUsers.keySet();
        for(Map.Entry<Long, UserGroup> userGroupEntry : existingUsers.entrySet()){
            UserGroup userGroup = userGroupEntry.getValue();
            Long userId = userGroup.getUserId();

            boolean exists = newUserIds.stream().anyMatch(newUserId -> newUserId.equals(userId));
            if (Boolean.TRUE.equals(exists)){

                //update manager on existing connection
                if(newUsers.get(userId).isManager() != userGroup.isManager()) {
                    userGroup.setManager(newUsers.get(userId).isManager());
                    userGroupsToUpdate.add(userGroup);
                }

                newUserIds.remove(userId);
                newUsers.remove(userId);
            } else {
                userGroupsToRemove.add(userGroup);
            }
        }

        //delete old connections
        if(!CollectionUtils.isEmpty(userGroupsToRemove)){
            userGroupRepository.deleteAll(userGroupsToRemove);
        }

        //add new connections
        Set<UserGroup> userGroups = newUsers.values().stream().map(newUser -> {
            return UserGroup.builder()
                    .groupId(groupId)
                    .isManager(newUser.isManager())
                    .userId(newUser.getId())
                    .build();
        }).collect(Collectors.toSet());

        //update existing connections
        userGroups.addAll(userGroupsToUpdate);

        if(!CollectionUtils.isEmpty(userGroups)) {
            userGroupRepository.saveAll(userGroups);
        }
    }
}
