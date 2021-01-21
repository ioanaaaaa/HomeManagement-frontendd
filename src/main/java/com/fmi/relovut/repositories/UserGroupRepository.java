package com.fmi.relovut.repositories;

import com.fmi.relovut.models.UserGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

    @Modifying
    void deleteByGroupId(Long groupId);

    @Query("select distinct u.email from UserGroup ug inner join ug.user u where ug.groupId in (:ids) and u.email =:email and ug.isManager = true")
    String findManagerByGroupIds(@Param("ids") Set<Long> grouopIds, @Param("email") String principalEmail);

    @Query("select ug.groupId from UserGroup ug where ug.userId=?1")
    Set<Long> findGroupIdsByUserId(Long userId);

    @Query("select ug.userId from UserGroup ug where ug.groupId=?1")
    Set<Long> findUserIdsByGroupId(Long groupId);

    @Query("select distinct ug.groupId from UserGroup ug where ug.userId =:userId and ug.isManager = true")
    Set<Long> findGroupIdsManagedByPrinciple(@Param("userId") Long userId);

}
