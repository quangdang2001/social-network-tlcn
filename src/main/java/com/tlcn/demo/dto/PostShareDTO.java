package com.tlcn.demo.dto;


import com.tlcn.demo.model.Post;
import lombok.Data;

@Data
public class PostShareDTO {
    private Post postShared;
    private Long userCreateId;
    private String firstName;
    private String lastName;
    private String avatar;
}
