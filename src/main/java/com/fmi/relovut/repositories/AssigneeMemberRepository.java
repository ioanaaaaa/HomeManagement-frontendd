package com.fmi.relovut.repositories;

import com.fmi.relovut.models.AssigneeMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssigneeMemberRepository extends CrudRepository<AssigneeMember, Long> {
}
