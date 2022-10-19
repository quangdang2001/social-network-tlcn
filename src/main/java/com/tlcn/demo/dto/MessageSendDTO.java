package com.tlcn.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class MessageSendDTO {
    private String content;
    private Long userSenderId;
    private Long userReceiverId;
}
