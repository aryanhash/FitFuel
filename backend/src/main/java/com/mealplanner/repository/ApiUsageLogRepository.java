package com.mealplanner.repository;

import com.mealplanner.entity.ApiUsageLog;
import com.mealplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ApiUsageLogRepository extends JpaRepository<ApiUsageLog, Long> {
    
    /**
     * Count API usage by API name, user, and date range
     */
    long countByApiNameAndUserAndCreatedAtAfter(String apiName, User user, LocalDateTime date);
    
    /**
     * Find usage logs by API name
     */
    java.util.List<ApiUsageLog> findByApiName(String apiName);
    
    /**
     * Find usage logs by user
     */
    java.util.List<ApiUsageLog> findByUser(User user);
    
    /**
     * Find usage logs by API name and user
     */
    java.util.List<ApiUsageLog> findByApiNameAndUser(String apiName, User user);
}
