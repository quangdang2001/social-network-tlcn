package com.tlcn.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @CreationTimestamp
    private Date createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private Users receiver;
    private String room;
    private String type;
    private String fileName;
}
