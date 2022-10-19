package com.tlcn.demo.service.iplm;


import com.tlcn.demo.dto.NotificationDTO;
import com.tlcn.demo.dto.UserDTO;
import com.tlcn.demo.model.Notification;
import com.tlcn.demo.model.Post;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.NotificationRepo;
import com.tlcn.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceIplm implements NotificationService {

    private final NotificationRepo notificationRepo;

    @Override
    public Notification save(Notification notification) {
        return notificationRepo.save(notification);
    }

    @Override
    public void deleteById(Long id) {
        notificationRepo.deleteById(id);
    }

    @Override
    public List<NotificationDTO> findNotificationByUserId(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Notification> notifications =
                notificationRepo.findAllByUserReceiver_IdOrderByCreateTimeDesc(userId,pageable);
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        notifications.forEach(notification -> {
            NotificationDTO notificationDTO = new NotificationDTO();
            UserDTO userDTO = new UserDTO();
            notificationDTO.setContent(notification.getContent());
            notificationDTO.setId(notification.getId());
            notificationDTO.setSeen(notification.isSeen());
            notificationDTO.setUserReceiver(notification.getUserReceiver().getId());
            notificationDTO.setCreateTime(notification.getCreateTime());
            if (notification.getPost()!= null) {
                notificationDTO.setPostId(notification.getPost().getId());
            }
            userDTO.setId(notification.getUserCreate().getId());
            userDTO.setLastName(notification.getUserCreate().getLastName());
            userDTO.setFirstName(notification.getUserCreate().getFirstName());
            userDTO.setImageUrl(notification.getUserCreate().getImageUrl());

            notificationDTO.setUserCreate(userDTO);
            notificationDTOS.add(notificationDTO);
        });
        return notificationDTOS;
    }

    @Override
    public Notification findById(Long id) {
        Optional<Notification> optional = notificationRepo.findById(id);
        return optional.orElse(null);
    }

    @Override
    public Long countNotSeenNotifi(Long userId) {
        Long count = notificationRepo.findNotificationByUserReceiver_Id(userId).stream().filter(notification ->
                !notification.isSeen()).count();
        return count;

    }

    @Override
    public Notification sendNotificationPost(Post post, Users senderId, String content) {
            Notification notification = new Notification();
            notification.setContent(content);
            notification.setUserReceiver(post.getUsers());
            notification.setUserCreate(senderId);
            notification.setPost(post);
            notification.setCreateTime(new Date());
            save(notification);
            return notification;
    }

    @Override
    public Notification sendNotificationFollow(Users user, Users userReceiver, String content) {
        Notification notification = new Notification();
        try {
            notification.setContent(content);
            notification.setUserCreate(user);
            notification.setUserReceiver(userReceiver);
            notification.setCreateTime(new Date());

            notificationRepo.save(notification);
        }catch (Exception e){
            return null;
        }
        return notification;
    }
}
