package com.mealplanner.service;

import com.mealplanner.entity.User;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.repository.UserRepository;
import com.mealplanner.repository.UserPreferencesRepository;
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
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    // Create a new user
    public User createUser(User user) {
        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        User savedUser = userRepository.save(user);
        
        // Create default preferences for the user
        UserPreferences preferences = new UserPreferences(savedUser);
        preferences.setDietType("VEGETARIAN");
        preferences.setDailyCalorieTarget(2000);
        preferences.setCookingSkillLevel("BEGINNER");
        userPreferencesRepository.save(preferences);
        
        return savedUser;
    }
    
    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Update user
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // Get user preferences
    public Optional<UserPreferences> getUserPreferences(User user) {
        return userPreferencesRepository.findByUser(user);
    }
    
    // Update user preferences
    public UserPreferences updateUserPreferences(User user, UserPreferences preferences) {
        preferences.setUser(user);
        return userPreferencesRepository.save(preferences);
    }
    
    // Check if user exists
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
