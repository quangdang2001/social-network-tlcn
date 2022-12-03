package com.tlcn.demo.repository;


import com.tlcn.demo.dto.ConversationDTO;
import com.tlcn.demo.model.Message;
import com.tlcn.demo.model.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findBySender_IdAndReceiver_IdOrderByCreateTimeDesc(Long senderId, Long receiverId, Pageable pageable);
    List<Message> findAllByRoomOrderByCreateTimeDesc(String room, Pageable pageable);
    @Query("select distinct m.sender,m.receiver,m.createTime from Message m where m.sender.id = :id or m.receiver.id = :id order by m.createTime desc")
    List<Users> findSenderChat(Long id, Pageable pageable);
    @Query("select distinct m.receiver,m.sender, m.createTime from Message m where m.sender.id = :id or m.receiver.id = :id order by m.createTime desc")
    List<Users> findReceiverChat(Long id, Pageable pageable);

    @Query(value = "SELECT DISTINCT RECEIVER_ID, SENDER_ID" +
            "FROM MESSAGE" +
            "WHERE SENDER_ID = :senderId",
    nativeQuery = true)
    List<ConversationDTO> getConversation(Long senderId, Pageable pageable);
}
