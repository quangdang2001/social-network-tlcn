package com.tlcn.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmtDTO {
    private Long id;
    private String content;

    private Long postId;
    private Long cmtId;
}
