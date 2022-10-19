package com.tlcn.demo.service;



import com.tlcn.demo.dto.MessageDTO;
import com.tlcn.demo.dto.UserChatDTO;

import java.util.List;

public interface MessageService {
    MessageDTO sendMessage(MessageDTO messageDTO);
    List<MessageDTO> getMessage(Long senderId, Long receiverId,Integer page, Integer size);
    boolean deleteMessage(Long messageId);
    List<UserChatDTO> findUserChat(Long userId, int page, int size);
}
