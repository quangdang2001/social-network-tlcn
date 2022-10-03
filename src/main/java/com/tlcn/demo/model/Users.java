package com.tlcn.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(indexes = {@Index(name = "index1", columnList = "firstName,lastName"),
        @Index(name = "index2", columnList = "email"),
        @Index(name = "index3",columnList = "lastName,firstName")})
@Data @AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String imageUrl;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    private int gender;
    private String bio;
    private String address;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date birthDay;
    private int countReport = 0;
    private int countFollower = 0;
    private int countFollowing =0;
    private boolean enable = false;
    private String role;
    private String provider;
    @JsonIgnore
    @OneToMany(mappedBy = "userReceiver",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @JsonIgnore
    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "sender",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Message> messages;

    @JsonIgnore
    @OneToMany(mappedBy = "userId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<PostLike> postLikes;


    @JsonIgnore
    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "userId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserFollowing> userFollowings;

    @JsonIgnore
    @OneToMany(mappedBy = "followingId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserFollowing> followingId;


}
