package com.tlcn.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private String message;
    private Date createTime;
    private Long senderId;
    private Long receiverId;
    private String senderAvatar;
    private String room;
    private List<MultipartFile> files;

    public MessageDTO(Long id, String message, Date createTime, Long senderId, Long receiverId, String senderAvatar, String room) {
        this.id = id;
        this.message = message;
        this.createTime = createTime;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderAvatar = senderAvatar;
        this.room = room;
    }

    @Data
    @AllArgsConstructor
    public static class Sender{
        private Long id;
        private String firstName;
        private String lastName;
        private String urlImage;
    }
    @Data
    @AllArgsConstructor
    public static class Receiver {
        private Long id;
        private String firstName;
        private String lastName;
        private String urlImage;
    }
}
