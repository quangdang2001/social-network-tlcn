package com.tlcn.demo.service.iplm;


import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import com.tlcn.demo.dto.CmtDTO;
import com.tlcn.demo.dto.CmtResponse;
import com.tlcn.demo.model.Comment;
import com.tlcn.demo.model.Notification;
import com.tlcn.demo.model.Post;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.CommentRepo;
import com.tlcn.demo.service.CommentService;
import com.tlcn.demo.service.NotificationService;
import com.tlcn.demo.service.PostService;
import com.tlcn.demo.service.UserService;
import com.tlcn.demo.util.Convert;
import com.tlcn.demo.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceIplm implements CommentService {

    private final CommentRepo commentRepo;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public Comment findById(Long id) {
        Optional<Comment> comment = commentRepo.findById(id);
        return comment.orElse(null);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepo.save(comment) ;
    }

    @Override
    public void deleteCommentById(Long id) {
        commentRepo.deleteById(id);
    }

    @Override
    public List<Comment> findCmtByPostId(Long postId, Pageable pageable) {
        return commentRepo.findCommentByPost_IdAndCommentParrentOrderByCreateTimeDesc(postId,null,pageable);
    }

//    @Override
//    public void likeComment(Long cmtId) {
//        Comment comment = findById(cmtId);
//        comment.increaseLike();
//        save(comment);
//    }

    @Override
    public CmtResponse cmtComment(CmtDTO cmtDTO){
        Long userId = Utils.getIdCurrentUser();

        Comment comment = new Comment();
        Comment cmtParent = findById(cmtDTO.getCmtId());
        Post post = postService.findPostById(cmtDTO.getPostId());
        Users users = userService.findById(userId);
        if (users.isEnable()) {
            comment.setContent(cmtDTO.getContent());
            comment.setCommentPost(false);
            comment.setCreateTime(new Date());
            comment.setUsers(users);
            comment.setPost(post);
            comment.setCommentParrent(cmtParent);
            cmtParent.setCountReply(cmtParent.getCountReply() + 1);
            save(cmtParent);
            save(comment);
            CmtResponse cmtDTO1 = Convert.convertCmtToRes(users, comment);

            if (!cmtParent.getUsers().getId().equals(userId)) {
                String content = String.format("%s %s replied your comment.", users.getFirstName(), users.getLastName());
                Notification notification = notificationService.sendNotificationPost(post, users, content);
                NotificationPayload notificationPayload = Convert.convertNotificationToNotifiPayload(notification);
                cmtDTO1.setNotificationPayload(notificationPayload);
            }
            return cmtDTO1;
        }
        return null;
    }

    @Override
    public CmtResponse cmtPost(CmtDTO cmtDTO){

        Long userId = Utils.getIdCurrentUser();

        Comment comment = new Comment();
        Post post = postService.findPostById(cmtDTO.getPostId());
        Users users = userService.findById(userId);
        if (users.isEnable()) {
            comment.setContent(cmtDTO.getContent());
            comment.setPost(post);
            comment.setUsers(users);
            comment.setCreateTime(new Date());
            post.setCountCmted(post.getCountCmted() + 1);
            commentRepo.save(comment);
            postService.save(post);
            CmtResponse cmtDTO1 = Convert.convertCmtToRes(users, comment);
            if (!post.getUsers().getId().equals(userId)) {
                String content = String.format("%s %s commented in your post.", users.getFirstName(), users.getLastName());
                Notification notification = notificationService.sendNotificationPost(post, users, content);
                NotificationPayload notificationPayload = Convert.convertNotificationToNotifiPayload(notification);
                cmtDTO1.setNotificationPayload(notificationPayload);
            }
            return cmtDTO1;
        }
        return null;
    }

    @Override
    public List<Comment> findCommentChild(Long cmtIdParent) {
        List<Comment> comments = commentRepo.findCommentByCommentParrent_IdOrderByCreateTimeDesc(cmtIdParent);
        return comments;
    }
}
