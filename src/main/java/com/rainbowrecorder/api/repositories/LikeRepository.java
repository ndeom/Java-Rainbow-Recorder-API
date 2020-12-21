package com.rainbowrecorder.api.repositories;

import com.rainbowrecorder.api.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, String> {
}
