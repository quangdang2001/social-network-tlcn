package com.tlcn.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(HashPostID.class)
public class HashPost {
    @Id
    @ManyToOne
    private Post postId;
    @Id
    @ManyToOne
    private Hashtag hashtagId;
}
