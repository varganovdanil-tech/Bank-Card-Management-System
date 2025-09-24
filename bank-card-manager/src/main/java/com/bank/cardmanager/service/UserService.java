package com.bank.cardmanager.service;

import com.bank.cardmanager.exception.ResourceNotFoundException;
import com.bank.cardmanager.model.User;
import com.bank.cardmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> getActiveUsers(Pageable pageable) {
        return userRepository.findByActiveTrue(pageable);
    }
    
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }
    
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }
    
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(true);
        userRepository.save(user);
    }
    
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    public long getActiveUserCount() {
        return userRepository.findByActiveTrue().size();
    }
}