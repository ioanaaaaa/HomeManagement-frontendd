package com.fmi.relovut.repositories;

import com.fmi.relovut.models.Assignee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssigneeRepository extends CrudRepository<Assignee, Long> {

    Assignee findByTaskId(Long taskId);
}
