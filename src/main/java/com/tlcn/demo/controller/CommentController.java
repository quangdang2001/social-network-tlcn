package com.tlcn.demo.controller;


import com.tlcn.demo.dto.CmtDTO;
import com.tlcn.demo.dto.CmtResponse;
import com.tlcn.demo.dto.ResponseDTO;
import com.tlcn.demo.model.Comment;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.service.CommentService;
import com.tlcn.demo.util.Convert;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "AUTHORIZATION")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<?> cmtPost(@RequestBody CmtDTO cmtDTO){
        CmtResponse cmtResponse = commentService.cmtPost(cmtDTO);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                cmtResponse));
    }

    @GetMapping("/comment/post/child/{cmtParentId}")
    public ResponseEntity<?> getCmmentChild(@PathVariable Long cmtParentId){
        try {
            List<Comment> comments= commentService.findCommentChild(cmtParentId);
            List<CmtResponse> cmtResponses = new ArrayList<>();
            comments.forEach(comment -> {
                Users users = comment.getUsers();

                CmtResponse cmtResponse = Convert.convertCmtToRes(users,comment);
                cmtResponses.add(cmtResponse);
            });
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                    cmtResponses));
        }
        catch (Exception e){
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                    e.getMessage()));
        }

    }
    @GetMapping("/comment/post")
    public ResponseEntity<?> getCommentPost(@RequestParam Long postId,
                                            @RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "5") Integer size){
        Pageable pageRequest = PageRequest.of(page,size);
        List<Comment> comments = commentService.findCmtByPostId(postId,pageRequest);
        List<CmtResponse> cmtResponses = new ArrayList<>();
        comments.forEach(comment -> {
            Users users = comment.getUsers();

            CmtResponse cmtResponse =Convert.convertCmtToRes(users,comment);
            cmtResponses.add(cmtResponse);
        });

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                cmtResponses));
    }
//    @PostMapping("/comment/like")
//    public ResponseEntity<?> likeCmt(@RequestParam Long cmtId){
//        commentService.likeComment(cmtId);
//        return ResponseEntity.ok().build();
//    }
    @PostMapping("/comment/child")
    public ResponseEntity<?> cmtChild(@RequestBody CmtDTO cmtDTO) throws InterruptedException {
        CmtResponse cmtResponse = commentService.cmtComment(cmtDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                cmtResponse));
    }

    @DeleteMapping("/comment/{cmtId}")
    public ResponseEntity<?> deleteCmt(@PathVariable Long cmtId){
        commentService.deleteCommentById(cmtId);
        return ResponseEntity.ok(new ResponseDTO(true,"Success",null));
    }


}
