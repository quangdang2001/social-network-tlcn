package com.tlcn.demo.service.iplm;


import com.tlcn.demo.dto.MessageDTO;
import com.tlcn.demo.dto.UserChatDTO;
import com.tlcn.demo.model.Message;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.MessageRepo;
import com.tlcn.demo.repository.UserRepo;
import com.tlcn.demo.service.MessageService;
import com.tlcn.demo.service.UserService;
import com.tlcn.demo.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceIplm implements MessageService {
    private final MessageRepo messageRepo;
    private final UserService userService;
    private final UserRepo userRepo;


    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO) {
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
        messageDTO.setRoom(getRoom(receiverId, senderId));
        messageDTO.setCreateTime(new Date());

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
