package com.rainbowrecorder.api.repositories;

import com.rainbowrecorder.api.models.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByUsername(String username);

    Optional<User> findById(String id);

    @Query(value = "SELECT profile_picture " +
                    "FROM main.users " +
                    "WHERE user_id = :userId", nativeQuery = true)
    Optional<String> getExistingProfilePicture(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE main.users " +
                    "SET username = :username " +
                    "WHERE user_id = :userId", nativeQuery = true)
    void updateUsername(@Param("userId") String userId, @Param("username") String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE main.users " +
                    "SET hash = :hash " +
                    "WHERE user_id = :userId", nativeQuery = true)
    void updatePassword(@Param("userId") String userId, @Param("hash") String hash);

    @Transactional
    @Modifying
    @Query(value = "UPDATE main.users " +
                    "SET screen_name = :screenName " +
                    "WHERE user_id = :userId", nativeQuery = true)
    void updateScreenName(@Param("userId") String userId, @Param("screenName") String screenName);

    @Transactional
    @Modifying
    @Query(value = "UPDATE main.users " +
                    "SET profile_picture = :profilePicture " +
                    "WHERE user_id = :userId", nativeQuery = true)
    void updateProfilePicture(@Param("userId") String userId, @Param("profilePicture") String profilePicture);

}