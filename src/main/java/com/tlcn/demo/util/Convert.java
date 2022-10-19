package com.tlcn.demo.util;


import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import com.tlcn.demo.controller.ws.Payload.UserPayload;
import com.tlcn.demo.dto.CmtResponse;
import com.tlcn.demo.model.Comment;
import com.tlcn.demo.model.Notification;
import com.tlcn.demo.model.Users;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Convert {
    private static String pathImg = "Images";
    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        if (!Files.isDirectory(Path.of(pathImg))){
            File newDir = new File(pathImg);
            newDir.mkdir();
        }
        File convFile = new File(pathImg+"/" +file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    public static String formatName(String name){
        name = name.trim();
        name = name.replaceAll("  "," ");
        return name;
    }
    public static CmtResponse convertCmtToRes(Users users, Comment comment){
        CmtResponse.User user = new CmtResponse.User(users.getId(),users.getFirstName(),users.getLastName(),
                users.getImageUrl());
        CmtResponse cmtResponse = CmtResponse.builder()
                .content(comment.getContent())
                .createTime(comment.getCreateTime())
                .cmtId(comment.getId())
                .userCmt(user)
                .postId(comment.getPost().getId())
                .countReply(comment.getCountReply())
                .build();
        if (comment.getCommentParrent()!=null){
            cmtResponse.setCmtParrentId(comment.getCommentParrent().getId());
        }
        return cmtResponse;
    }
    public static NotificationPayload convertNotificationToNotifiPayload(Notification notification){
        Users users = notification.getUserCreate();
        UserPayload userPayload = new UserPayload(users.getFirstName(), users.getLastName(), users.getId(), users.getImageUrl());
        NotificationPayload notificationPayload = new NotificationPayload();
        notificationPayload.setContent(notification.getContent());
        notificationPayload.setCreateTime(notification.getCreateTime());
        if (notification.getPost()!=null) {
            notificationPayload.setPostId(notification.getPost().getId());
        }
        notificationPayload.setUserCreate(userPayload);
        notificationPayload.setUserReceiverId(notification.getUserReceiver().getId());
        return notificationPayload;
    }

}
