package com.fmi.relovut.repositories;

import com.fmi.relovut.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);

    @Query("select u.id from User u where u.email=?1")
    Long findIdByEmail(String email);

    List<User> findTop100ByFullnameContaining(String fullname);

    @Query("select u from User u where u.id in (:userIds)")
    Set<User> findByIds(@Param("userIds") Set<Long> userIds);
}
