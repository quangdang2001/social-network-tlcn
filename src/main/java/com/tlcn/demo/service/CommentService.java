package com.tlcn.demo.service;


import com.tlcn.demo.dto.CmtDTO;
import com.tlcn.demo.dto.CmtResponse;
import com.tlcn.demo.model.Comment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Comment findById(Long id);
    Comment save(Comment comment);
    void deleteCommentById(Long id);
    List<Comment> findCmtByPostId(Long postId, Pageable pageable);
    /////
//    void likeComment(Long cmtId);
    CmtResponse cmtComment(CmtDTO cmtDTO);
    CmtResponse cmtPost(CmtDTO cmtDTO);
    List<Comment> findCommentChild(Long cmtIdParent);

}
