package com.tlcn.demo.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class PostLikeID implements Serializable {
    private Long postId;
    private Long userId;
}
