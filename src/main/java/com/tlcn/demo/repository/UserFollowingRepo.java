package com.tlcn.demo.repository;


import com.tlcn.demo.model.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFollowingRepo extends JpaRepository<UserFollowing,Long> {
    List<UserFollowing> findAllByUserId_Id(Long userId);
    List<UserFollowing> findAllByFollowingId_Id(Long userId);
    UserFollowing findByUserId_IdAndFollowingId_Id(Long userId,Long followingId);
}
