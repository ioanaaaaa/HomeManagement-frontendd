package com.fmi.relovut.repositories;

import com.fmi.relovut.models.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Query(" select distinct t from Task t left join fetch t.activeAssignedUsers a inner join fetch a.assigneeMemberSet am " +
            "where t.id =:id")
    Optional<Task> findByTaskIdEagerlyActive(@Param("id") Long taskId);

    @Query("select t from Task t inner join fetch t.activeAssignedUsers a inner join fetch a.activeAssigneeMemberSet am")
    Set<Task> findAll();

    @Query("select distinct t from Task t inner join fetch t.activeAssignedUsers a inner join fetch a.activeAssigneeMemberSet am " +
            "where (am.userId =:id or am.groupId in (:ids)) and a.active = true and am.active = true and t.status = 'IN_PROGRESS'")
    Set<Task> findAllActiveForUser(@Param("id") Long userId, @Param("ids") Set<Long> groupIds);

    @Query("select distinct t from Task t inner join fetch t.activeAssignedUsers a inner join fetch a.activeAssigneeMemberSet am " +
            "where am.userId =:id  and t.status = 'COMPLETED'")
    Set<Task> findAllCompletedForUser(@Param("id") Long userId);

    List<Task> findByActiveAssignedUsers_activeAssigneeMemberSet_groupIdIn(Set<Long> groupIds);

    List<Task> findAll(Specification<Task> taskFilterDto);
}
