package com.tlcn.demo.repository;


import com.tlcn.demo.model.Post;
import com.tlcn.demo.model.PostLike;
import com.tlcn.demo.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepo extends JpaRepository<PostLike,Long> {
    PostLike findByPostIdAndUserId(Post postId, Users userId);

    Boolean existsByPostIdAndUserId_Id(Post postId, Long userId);
}
