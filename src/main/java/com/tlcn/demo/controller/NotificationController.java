package com.tlcn.demo.controller;


import com.tlcn.demo.dto.ResponseDTO;
import com.tlcn.demo.model.Notification;
import com.tlcn.demo.service.NotificationService;
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
public class NotificationController {

    private final NotificationService notificationService;

    @PutMapping("notification/setSeen")
    public ResponseEntity<?> setSeen(@RequestParam Long notificationId){
       Notification notification = notificationService.findById(notificationId);
       notification.setSeen(true);
       notificationService.save(notification);
       return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true,"Success",
                null));
    }
}
