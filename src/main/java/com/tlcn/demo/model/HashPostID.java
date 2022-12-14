package com.tlcn.demo.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class HashPostID implements Serializable {
    private Long hashtagId;
    private Long postId;
}
