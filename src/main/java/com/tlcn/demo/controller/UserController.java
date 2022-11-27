package com.tlcn.demo.controller;


import com.tlcn.demo.controller.ws.Payload.NotificationPayload;
import com.tlcn.demo.dto.*;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.service.NotificationService;
import com.tlcn.demo.service.UserFollowingService;
import com.tlcn.demo.service.UserService;
import com.tlcn.demo.util.Convert;
import com.tlcn.demo.util.Utils;
import com.tlcn.demo.util.contant.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "AUTHORIZATION")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserFollowingService userFollowingService;
    private final NotificationService notificationService;

    @PostMapping(path = "/user/upimg",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upUserImgProfile(@RequestParam("img")MultipartFile file) throws IOException {
        String imgUrl = userService.upImageProfile(file);

        return ResponseEntity.ok().body(new ResponseDTO(true,"Success",
                imgUrl));
    }

    @GetMapping("/user")
    private ResponseEntity<?> getUser(){
        Long userId = Utils.getIdCurrentUser();
        Users users = userService.findById(userId);

        return ResponseEntity.ok().body(new ResponseDTO(true,"Success",
                users));

    }

    @PutMapping("/user")
    private ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO){
        Users users = userService.updateUser(userDTO);
        return ResponseEntity.ok().body(new ResponseDTO(true,"Success",
                users));
    }


    @PostMapping("/user/registerTest")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDTO userDTO){
        Users user;
        user = userService.findUserByEmail(userDTO.getEmail());
        if (user!=null){
            return null;
        }
        user = new Users();
        user.setFirstName(Convert.formatName(userDTO.getFirstName()));
        user.setLastName(Convert.formatName(userDTO.getLastName()));
        user.setAddress(userDTO.getAddress());
        user.setBio(userDTO.getBio());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setBirthDay(userDTO.getBirthDay());
        user.setEnable(true);
        user.setRole(Constant.ROLE_ADMIN);
        userService.save(user);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/user/topFollower")
    public ResponseEntity<?> getUserTop(){
        return ResponseEntity.ok(new ResponseDTO(true,"Success",userFollowingService.top10Follower()));
    }

    @GetMapping("/user/notification")
    private ResponseEntity<?> getNotifi(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size){
        Long userId = Utils.getIdCurrentUser();
        List<NotificationDTO> notificationDTOS = notificationService.findNotificationByUserId(userId,page,size);

        return ResponseEntity.ok().body(new ResponseDTO(true,"Success",
                notificationDTOS));
    }

}
