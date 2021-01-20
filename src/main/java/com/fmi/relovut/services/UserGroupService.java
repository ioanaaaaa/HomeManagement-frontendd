package com.fmi.relovut.services;

import com.fmi.relovut.repositories.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;

    @Autowired
    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    @Transactional(readOnly = true)
    public boolean checkForManager(Set<Long> groupIds, String principalEmail){
        String email = userGroupRepository.findManagerByGroupIds(groupIds, principalEmail);
        return StringUtils.isEmpty(email) ? false : true;
    }

    public Set<Long> getGroupsIdsForUser(Long userId){
        return userGroupRepository.findGroupIdsByUserId(userId);
    }

    public Set<Long> getUserIdsForGroup(Long groupId){
        return userGroupRepository.findUserIdsByGroupId(groupId);
    }
}
