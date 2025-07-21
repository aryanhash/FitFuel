package com.mealplanner.service;

import com.mealplanner.entity.User;
import com.mealplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Save a new user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Update an existing user
     */
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setDietPreference(userDetails.getDietPreference());
            // Don't update password here - use separate method for password changes
            return userRepository.save(user);
        }
        return null;
    }
    
    /**
     * Update user password
     */
    public boolean updatePassword(Long userId, String newPasswordHash) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPasswordHash(newPasswordHash);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    /**
     * Delete a user
     */
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Authenticate user
     */
    public Optional<User> authenticateUser(String email, String passwordHash) {
        return userRepository.findByEmailAndPasswordHash(email, passwordHash);
    }
} 