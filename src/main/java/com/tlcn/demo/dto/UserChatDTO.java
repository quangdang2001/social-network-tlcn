package com.tlcn.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserChatDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String email;
    private String room;
}
