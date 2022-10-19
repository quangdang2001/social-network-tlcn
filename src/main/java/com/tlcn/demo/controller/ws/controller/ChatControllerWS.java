package com.tlcn.demo.controller.ws.controller;


import com.tlcn.demo.controller.ws.Payload.MessagePayload;
import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatControllerWS {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@Payload MessagePayload messagePayload){
        simpMessagingTemplate.convertAndSendToUser(messagePayload.getReceiverId().toString(),"/chat",messagePayload);
        NotificationPayload notificationPayload = new NotificationPayload();
        notificationPayload.setType("CHAT");
        notificationPayload.setContent(messagePayload.getFullName()+" sent you a message.");
        simpMessagingTemplate.convertAndSendToUser(messagePayload.getReceiverId().toString(),
                "/notificationPopUp",notificationPayload);
        return ResponseEntity.ok(messagePayload);
    }
}
