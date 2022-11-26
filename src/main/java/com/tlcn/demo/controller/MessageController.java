package com.tlcn.demo.controller;


import com.tlcn.demo.dto.MessageDTO;
import com.tlcn.demo.dto.ResponseDTO;
import com.tlcn.demo.dto.UserChatDTO;
import com.tlcn.demo.service.MessageService;
import com.tlcn.demo.util.Utils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "AUTHORIZATION")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(path = "/message")
    private ResponseEntity<?> sendMessage(@ModelAttribute MessageDTO messageDTO) throws IOException {
        MessageDTO message =messageService.sendMessage(messageDTO, messageDTO.getFiles());
        if (message!=null){

            return ResponseEntity.ok(new ResponseDTO(true,"Success",message));
        }else {
            return ResponseEntity.ok(new ResponseDTO(false,"Failed",null));
        }
    }

    @GetMapping("/message")
    private ResponseEntity<?> getMessage(@RequestParam Long receiverId,
                                         @RequestParam int page,
                                         @RequestParam int size){
        Long userId = Utils.getIdCurrentUser();
        List<MessageDTO> messageDTOList = messageService.getMessage(userId,receiverId,page,size);
        return ResponseEntity.ok(new ResponseDTO(true,"Success", messageDTOList));
    }

    @DeleteMapping("/message/{messageId}")
    private ResponseEntity<?> deleteMessage(@PathVariable Long messageId){
        boolean check = messageService.deleteMessage(messageId);
        if (check){
            return ResponseEntity.ok(new ResponseDTO(true,"Success",null));
        }
        else return ResponseEntity.ok(new ResponseDTO(false,"Failed",null));
    }
    @GetMapping("/message/conversations")
    private ResponseEntity<?> getConversations(@RequestParam int page,
                                               @RequestParam int size){
        Long userId = Utils.getIdCurrentUser();
        List<UserChatDTO> userChatDTOS = messageService.findUserChat(userId,page,size);
        return ResponseEntity.ok(
                new ResponseDTO(true,"Success",userChatDTOS));

    }

}
