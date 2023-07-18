package com.genug.murmur.api.repository;

import com.genug.murmur.api.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    // Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
}
