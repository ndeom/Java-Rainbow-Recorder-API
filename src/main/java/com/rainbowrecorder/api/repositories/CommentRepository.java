package com.rainbowrecorder.api.repositories;

import com.rainbowrecorder.api.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {
}
