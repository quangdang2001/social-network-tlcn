package com.tlcn.demo.controller;


import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import com.tlcn.demo.dto.PostDTO;
import com.tlcn.demo.dto.ResponseDTO;
import com.tlcn.demo.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "AUTHORIZATION")
public class PostShareController {
    private final PostService postService;
    @PostMapping("/postshare")
    public ResponseEntity<?> savePostShare(@RequestBody PostDTO postDTO){

        NotificationPayload notificationPayload= postService.sharePost(postDTO);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                notificationPayload));
    }
}
