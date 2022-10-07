package com.tlcn.demo.controller;

import com.tlcn.demo.dto.PostDTO;
import com.tlcn.demo.dto.PostReq;
import com.tlcn.demo.dto.ResponseDTO;
import com.tlcn.demo.model.Post;
import com.tlcn.demo.service.PostService;
import com.tlcn.demo.util.Utils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "AUTHORIZATION")
public class PostController {
    private final PostService postService;

    @GetMapping("/post")
    public ResponseEntity<?> getPostById(@RequestParam Long postId) throws ParseException {
        PostDTO postDTO = postService.findPostDTOById(postId);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                postDTO));
    }

    @PostMapping(path = "/post")
    public ResponseEntity<?> savePost(@RequestBody PostReq postReq)  {
        Long userId = Utils.getIdCurrentUser();
        Post post = postService.saveNewPost(postReq,userId);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                post));
    }

    @PutMapping(path = "/post/upImg",consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upImg(@RequestParam(value = "img")MultipartFile file,
                                   @RequestParam Long postId) throws IOException {
        String imgUrl = postService.upImagePost(file, postId);
        if (imgUrl!=null)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",imgUrl));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(false,"Failed",null));
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
        try {
            postService.deletePostById(id);
            return ResponseEntity.ok(new ResponseDTO(true,"Delete successfully",null));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ResponseDTO(false,e.getMessage(),null));

        }
    }

    @GetMapping("/post/user")
    public ResponseEntity<?> getAllPostUser(@RequestParam(defaultValue = "-1") Long userId,
                                            @RequestParam(value = "page",defaultValue = "0") Integer page,
                                            @RequestParam(value = "size",defaultValue = "10") Integer size){
        List<PostDTO> posts;
        Long currentUserId = Utils.getIdCurrentUser();
        if (userId.equals(currentUserId))
            posts = postService.findPostOfUser(userId,-1L,page,size);
        else posts = postService.findPostOfUser(userId,currentUserId,page,size);

        return ResponseEntity.ok(new ResponseDTO(true,"Success",posts));
    }

    @GetMapping("/post/homepage")
    public ResponseEntity<?> getPostHomepage(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                             @RequestParam(value = "size",defaultValue = "10") Integer size){
        List<PostDTO> posts;
        Long userId = Utils.getIdCurrentUser();
        posts = postService.findPostHomePage(userId,page, size);
        return ResponseEntity.ok(new ResponseDTO(true,"Success",posts));
    }

    @PostMapping("/post/like")
    public ResponseEntity<?> likePost(@RequestParam Long postId) throws InterruptedException {
        Long userId = Utils.getIdCurrentUser();
        boolean check = postService.likePost(userId, postId);
        if (check) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true, "Success", null));
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true, "Success", null));
        }
    }

}
