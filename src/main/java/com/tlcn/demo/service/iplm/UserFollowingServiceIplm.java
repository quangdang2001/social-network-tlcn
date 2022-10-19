package com.tlcn.demo.service.iplm;

import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import com.tlcn.demo.dto.UserFollowDTO;
import com.tlcn.demo.model.Notification;
import com.tlcn.demo.model.UserFollowing;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.UserFollowingRepo;
import com.tlcn.demo.repository.UserRepo;
import com.tlcn.demo.service.NotificationService;
import com.tlcn.demo.service.UserFollowingService;
import com.tlcn.demo.util.Convert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowingServiceIplm implements UserFollowingService {

    private final UserFollowingRepo userFollowingRepo;
    private final NotificationService notificationService;
    private final UserRepo userRepo;
    @Override
    public List<UserFollowDTO> findAllFollowingUser(Long userId) {
        List<UserFollowing> userFollowings = userFollowingRepo.findAllByUserId_Id(userId);
        List<Users> users = new ArrayList<>();
        userFollowings.forEach(userFollowing -> {
            users.add(userFollowing.getFollowingId());
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

    @Override
    public List<UserFollowDTO> findAllFollowerUser(Long userId) {
        List<UserFollowing> userFollowings = userFollowingRepo.findAllByFollowingId_Id(userId);
        List<Users> users = new ArrayList<>();
        userFollowings.forEach(userFollowing -> {
            users.add(userFollowing.getUserId());
        });

        return convertUsersToUserFollowDTO(users);
    }

    @Override
    public List<Long> findAllIdFollower(Long userId) {
        List<UserFollowing> userFollowings = userFollowingRepo.findAllByFollowingId_Id(userId);
        List<Long> usersId = new ArrayList<>();
        userFollowings.forEach(userFollowing -> {
            usersId.add(userFollowing.getUserId().getId());
        });
        return usersId;
    }

    @Override
    public NotificationPayload save(Users users, Users following) {
        UserFollowing userFollowing = new UserFollowing(users,following);
        String content = String.format("%s %s followed you.",users.getLastName(), users.getFirstName());
        Notification notification= notificationService.sendNotificationFollow(users,following,content);
        userFollowingRepo.save(userFollowing);

        return Convert.convertNotificationToNotifiPayload(notification);
    }

    @Override
    public void delete(Users users, Users following) {
        UserFollowing userFollowing = new UserFollowing(users,following);
        userFollowingRepo.delete(userFollowing);
    }

    @Override
    public UserFollowing checkFollow(Long userId, Long followingId) {
        return userFollowingRepo.findByUserId_IdAndFollowingId_Id(userId,followingId);
    }



    @Override
    public List<UserFollowDTO> top10Follower() {
        Pageable pageable = PageRequest.of(0,5);
        List<Users> users =  userRepo.findTop10Follower(pageable);

        List<UserFollowDTO> userFollowDTOS = new ArrayList<>();
        users.forEach(user ->{
            userFollowDTOS.add(new UserFollowDTO(user.getId(),user.getFirstName(),user.getLastName(),
                    user.getEmail(),user.getImageUrl(),user.getCountFollower()));
        } );
        return userFollowDTOS;
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
