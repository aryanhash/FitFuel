package com.mealplanner.repository;

import com.mealplanner.entity.AiConversation;
import com.mealplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiConversationRepository extends JpaRepository<AiConversation, Long> {
    
    /**
     * Find conversations by user and session ID
     */
    List<AiConversation> findByUserAndSessionIdOrderByCreatedAtAsc(User user, String sessionId);
    
    /**
     * Find all conversations for a user
     */
    List<AiConversation> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find conversations by session ID
     */
    List<AiConversation> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
