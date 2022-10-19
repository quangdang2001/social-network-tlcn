package com.tlcn.demo.controller.ws.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload {
    private Long id;
    private String content;
    private Date createTime;
    private Long postId;
    private Long userReceiverId;
    private UserPayload userCreate;
    private String type;
}
