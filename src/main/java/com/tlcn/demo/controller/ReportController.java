package com.tlcn.demo.controller;

import com.tlcn.demo.dto.ResponseDTO;
import com.tlcn.demo.service.PostService;
import com.tlcn.demo.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@SecurityRequirement(name = "AUTHORIZATION")
public class ReportController {

    private final PostService postService;
    private final UserService userService;

    @PutMapping("/report/post/{postId}")
    private ResponseEntity<?> reportPost(@PathVariable Long postId){
        Boolean report = postService.reportPost(postId);
        if (report){
            return ResponseEntity.ok(new ResponseDTO(true,"Report success",null));
        }
        return ResponseEntity.ok(new ResponseDTO(false,"Report failed",null));
    }
    @PutMapping("/report/user/{userId}")
    private ResponseEntity<?> reportUser(@PathVariable Long userId){
        Boolean report = userService.reportUser(userId);
        if (report){
            return ResponseEntity.ok(new ResponseDTO(true,"Report success",null));
        }
        return ResponseEntity.ok(new ResponseDTO(false,"Report failed",null));
    }

}
