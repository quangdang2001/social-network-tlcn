package com.tlcn.demo.repository;

import com.tlcn.demo.model.HashPost;
import com.tlcn.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashPostRepo extends JpaRepository<HashPost, Long> {
    @Query("select h.postId from HashPost h where h.hashtagId.hashtag = :hashtag order by h.postId.countLiked desc")
    List<Post> searchPostByHashtag(String hashtag);
}
