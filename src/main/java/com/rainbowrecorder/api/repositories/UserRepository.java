package com.rainbowrecorder.api.repositories;

import com.auth0.jwt.interfaces.Claim;
import com.rainbowrecorder.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByUsername(String username);

    Optional<User> findById(String id);

}