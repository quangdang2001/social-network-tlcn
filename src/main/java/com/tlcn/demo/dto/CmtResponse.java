package com.tlcn.demo.dto;


import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
public class CmtResponse {
    private Long cmtId;
    private String content;
    private Date createTime;
    private User userCmt;
    private Long cmtParrentId =null;
    private Long postId;
    private Integer countReply;
    private NotificationPayload notificationPayload;
    @AllArgsConstructor
    @Data
    public static class User {
        private Long id;
        private String firstName;
        private String lastName;
        private String urlImage;
    }
}



