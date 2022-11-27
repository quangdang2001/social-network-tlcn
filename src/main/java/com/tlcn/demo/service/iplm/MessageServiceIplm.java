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
    private final UserService userService;
    private final UserRepo userRepo;
    private final CloudinaryUpload cloudinaryUpload;

    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO, List<MultipartFile> files) throws IOException {
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
        messageRepo.save(message);
        if (files != null) {
            files.forEach(file -> {
                Message messageFile = new Message();
                String url = null;
                if (!file.isEmpty()) {
                    try {
                        url = cloudinaryUpload.upload(file, FolderName.FILE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    messageFile.setMessage(url);
                    messageFile.setCreateTime(new Date());
                    messageFile.setSender(usersSend);
                    messageFile.setReceiver(usersReceiver);
                    messageFile.setRoom(getRoom(receiverId, senderId));
                    messageRepo.save(messageFile);
                    messageDTO.setMessage(messageDTO.getMessage() + "||" + url);
                }
            });
        }
        messageDTO.setRoom(getRoom(receiverId, senderId));
        messageDTO.setCreateTime(new Date());
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
}
