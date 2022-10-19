package com.tlcn.demo.service;



import com.tlcn.demo.dto.NotificationDTO;
import com.tlcn.demo.model.Notification;
import com.tlcn.demo.model.Post;
import com.tlcn.demo.model.Users;

import java.util.List;

public interface NotificationService {
    Notification save(Notification notification);

    void deleteById(Long id);

    List<NotificationDTO> findNotificationByUserId(Long userId, Integer page, Integer size);

    Notification findById(Long id);

    Long countNotSeenNotifi(Long userId);

    Notification sendNotificationPost(Post post, Users senderId, String content);

    Notification sendNotificationFollow(Users user, Users userReceiver, String content);

}
