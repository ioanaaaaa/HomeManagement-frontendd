package com.fmi.relovut.repositories;

import com.fmi.relovut.models.Group;
import com.fmi.relovut.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
    @Query("select distinct g from Group g left join fetch UserGroup ug on (g.id = ug.groupId) ")
    List<Group> findAll();


    @Query("select g from Group g where g.id in (:groupIds)")
    Set<Group> findByIds(@Param("groupIds") Set<Long> groupIds);
}
