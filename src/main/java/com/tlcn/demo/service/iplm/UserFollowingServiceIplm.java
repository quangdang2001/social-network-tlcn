package com.tlcn.demo.service.iplm;

import com.tlcn.demo.dto.UserFollowDTO;
import com.tlcn.demo.model.UserFollowing;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.UserFollowingRepo;
import com.tlcn.demo.service.UserFollowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowingServiceIplm implements UserFollowingService {

    private final UserFollowingRepo userFollowingRepo;

    @Override
    public List<UserFollowDTO> findAllFollowingUser(Long userId) {
        List<UserFollowing> userFollowings = userFollowingRepo.findAllByFollowingId_Id(userId);
        List<Users> users = new ArrayList<>();
        userFollowings.forEach(userFollowing -> {
            users.add(userFollowing.getUserId());
        });

        return convertUsersToUserFollowDTO(users);
    }

    @Override
    public List<Users> findAllIdFollowingUser(Long userId) {
        List<UserFollowing> userFollowings = userFollowingRepo.findAllByUserId_Id(userId);
        List<Users> usersId = new ArrayList<>();
        userFollowings.forEach(userFollowing -> {
            usersId.add(userFollowing.getFollowingId());
        });
        return usersId;
    }

    private List<UserFollowDTO> convertUsersToUserFollowDTO(List<Users> users){
        List<UserFollowDTO> userDTOList = new ArrayList<>();
        users.forEach(user -> {
            userDTOList.add(new UserFollowDTO(user.getId(),user.getFirstName(),user.getLastName(),
                    user.getEmail(),user.getImageUrl(),user.getCountFollower()));
        });
        return userDTOList;
    }

}
