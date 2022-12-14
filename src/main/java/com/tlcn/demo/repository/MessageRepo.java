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

    @Query(value = "SELECT RECEIVER_ID AS receiverId, MAX(CREATE_TIME) AS createTime\n" +
            "FROM MESSAGE WHERE SENDER_ID = :senderId\n" +
            "GROUP BY RECEIVER_ID\n" +
            "ORDER BY createTime DESC",
    nativeQuery = true)
    List<ConversationDTO> getConversation(Long senderId, Pageable pageable);
}
