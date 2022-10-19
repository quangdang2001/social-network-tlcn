package com.tlcn.demo.service;



import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import com.tlcn.demo.dto.UserFollowDTO;
import com.tlcn.demo.model.UserFollowing;
import com.tlcn.demo.model.Users;

import java.util.List;

public interface UserFollowingService {
    List<UserFollowDTO> findAllFollowingUser(Long userId);
    List<Users> findAllIdFollowingUser(Long userId);
    List<UserFollowDTO> findAllFollowerUser(Long userId);
    List<Long> findAllIdFollower(Long userId);
    NotificationPayload save(Users users, Users following);
    void delete(Users users, Users following);
    UserFollowing checkFollow(Long userId, Long followingId);

    List<UserFollowDTO> top10Follower();
}
