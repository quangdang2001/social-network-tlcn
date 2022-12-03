package com.tlcn.demo.service.iplm;


import com.tlcn.demo.dto.MessageDTO;
import com.tlcn.demo.dto.UserChatDTO;
import com.tlcn.demo.model.Message;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.MessageRepo;
import com.tlcn.demo.repository.UserRepo;
import com.tlcn.demo.service.Cloudinary.CloudinaryUpload;
import com.tlcn.demo.service.MessageService;
import com.tlcn.demo.service.UserService;
import com.tlcn.demo.util.Utils;
import com.tlcn.demo.util.contant.FolderName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceIplm implements MessageService {
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final CloudinaryUpload cloudinaryUpload;
    private final ModelMapper modelMapper;

    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO, List<MultipartFile> files) throws IOException {
        Message message;
        if (messageDTO.getMessage()!=null){
             message = sendText(messageDTO);
        }
        else message = sendFile(messageDTO, files);
        modelMapper.map(message, messageDTO);
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setReceiverId(message.getReceiver().getId());
        messageDTO.setSenderAvatar(message.getSender().getImageUrl());
        messageDTO.setFiles(null);
        return messageDTO;
    }

    @Override
    public List<MessageDTO> getMessage(Long senderId, Long receiverId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        String room = getRoom(receiverId, senderId);
        List<Message> messages = messageRepo.findAllByRoomOrderByCreateTimeDesc(room, pageable);
        List<MessageDTO> messageDTOS = new ArrayList<>();
        messages.forEach(message -> {
            messageDTOS.add(new MessageDTO(message.getId(), message.getMessage(),
                    message.getCreateTime(), message.getSender().getId(),
                    message.getReceiver().getId(), message.getSender().getImageUrl(), room));
        });
        Collections.reverse(messageDTOS);
        return messageDTOS;
    }


    @Override
    public boolean deleteMessage(Long messageId) {
        try {
            messageRepo.deleteById(messageId);
            return true;
        } catch (Exception e) {
            log.info("Message exception: ", e.getMessage());
            return false;
        }
    }

    @Override
    public List<UserChatDTO> findUserChat(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Users> usersSender = messageRepo.findSenderChat(userId, pageable);
        List<Users> userReceiver = messageRepo.findReceiverChat(userId, pageable);
        int length = userReceiver.size();
        List<Users> conversations = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (!usersSender.get(i).getId().equals(userId)) {
                conversations.add(usersSender.get(i));
            } else
                conversations.add(userReceiver.get(i));
        }

        conversations = conversations.stream().distinct().collect(Collectors.toList());
        List<UserChatDTO> userChatDTOS = new ArrayList<>();
        conversations.forEach(user -> {
            userChatDTOS.add(new UserChatDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getImageUrl()
                    , user.getEmail(), getRoom(userId, user.getId())));
        });
        return userChatDTOS;
    }

    public String getRoom(Long receiverId, Long senderId) {
        return String.valueOf(receiverId + senderId) + String.valueOf(receiverId * senderId);
    }

    private Message sendText(MessageDTO messageDTO) {
        Long userId = Utils.getIdCurrentUser();
        Message message = new Message();
        if (!userRepo.existsById(userId) || !userRepo.existsById(messageDTO.getReceiverId()))
            return null;
        Users usersSend = userRepo.getById(userId);
        Users usersReceiver = userRepo.getById(messageDTO.getReceiverId());

        message.setMessage(messageDTO.getMessage());
        message.setCreateTime(new Date());
        message.setSender(usersSend);
        message.setReceiver(usersReceiver);
        Long receiverId = messageDTO.getReceiverId();
        Long senderId = userId;
        message.setRoom(getRoom(receiverId, senderId));
        message.setType("TEXT");
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            messageRepo.save(message);
        }
        return message;
    }

    private Message sendFile(MessageDTO messageDTO, List<MultipartFile> files){
        Long userId = Utils.getIdCurrentUser();
        Message messageFile = new Message();
        if (!userRepo.existsById(userId) || !userRepo.existsById(messageDTO.getReceiverId()))
            return null;
        Users usersSend = userRepo.getById(userId);
        Users usersReceiver = userRepo.getById(messageDTO.getReceiverId());
        Long receiverId = messageDTO.getReceiverId();
        if (files != null) {
            files.forEach(file -> {
                String url = null;
                Map map = null;
                if (!file.isEmpty()) {
                    try {
                        map = cloudinaryUpload.upload(file, FolderName.FILE);
                        url = (String) map.get("secure_url");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    messageFile.setType(map.get("resource_type").equals("image") ? "IMAGE" : "FILE");
                    messageFile.setFileName(file.getOriginalFilename());
                    messageFile.setMessage(url);
                    messageFile.setCreateTime(new Date());
                    messageFile.setSender(usersSend);
                    messageFile.setReceiver(usersReceiver);
                    messageFile.setRoom(getRoom(receiverId, userId));
                    messageRepo.save(messageFile);
                }
            });
        }
        return messageFile;
    }
}
