package com.fmi.relovut.repositories;

import com.fmi.relovut.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @EntityGraph(attributePaths = {"friends"})
    User findByEmail(String email);
    List<User> findTop100ByFullnameContaining(String fullname);
}
