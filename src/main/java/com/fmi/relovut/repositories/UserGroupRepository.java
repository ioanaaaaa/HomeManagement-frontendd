package com.fmi.relovut.repositories;

import com.fmi.relovut.models.UserGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

    @Modifying
    void deleteByGroupId(Long groupId);
}
