package com.tlcn.demo.service;



import com.tlcn.demo.dto.UserFollowDTO;
import com.tlcn.demo.model.UserFollowing;
import com.tlcn.demo.model.Users;

import java.util.List;

public interface UserFollowingService {
    List<UserFollowDTO> findAllFollowingUser(Long userId);
    List<Users> findAllIdFollowingUser(Long userId);
}
