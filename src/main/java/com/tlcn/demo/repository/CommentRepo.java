package com.tlcn.demo.repository;


import com.tlcn.demo.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepo extends JpaRepository<Comment,Long> {
    List<Comment> findCommentByPost_IdAndCommentParrentOrderByCreateTimeDesc(Long id, Comment comment , Pageable pageable);
    List<Comment> findCommentByCommentParrent_IdOrderByCreateTimeDesc(Long id);
}
