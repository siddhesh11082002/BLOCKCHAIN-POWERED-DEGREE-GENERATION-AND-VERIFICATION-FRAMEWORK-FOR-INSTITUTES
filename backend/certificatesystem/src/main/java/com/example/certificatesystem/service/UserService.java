package com.example.certificatesystem.service;

import com.example.certificatesystem.model.User;
import com.example.certificatesystem.model.UserDTO;
import com.example.certificatesystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Check if the user is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is not active: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_ADMIN") // For simplicity, all users are admins
                .build();
    }

    public boolean hasUsers() {
        return userRepository.count() > 0;
    }

    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Transactional
    public UserDTO.Response createUser(UserDTO.CreateRequest createRequest) {
        if (userRepository.existsByUsername(createRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setUsername(createRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createRequest.getPassword()));
        user.setFullName(createRequest.getFullName());
        user.setEmail(createRequest.getEmail());
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getUsername());
        
        return UserDTO.Response.fromUser(savedUser);
    }

    public List<UserDTO.Response> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO.Response::fromUser)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO.Response> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO.Response::fromUser);
    }

    public Optional<UserDTO.Response> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO.Response::fromUser);
    }

    @Transactional
    public Optional<UserDTO.Response> updateUser(Long id, UserDTO.UpdateRequest updateRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    if (updateRequest.getFullName() != null && !updateRequest.getFullName().isBlank()) {
                        user.setFullName(updateRequest.getFullName());
                    }
                    
                    if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank()) {
                        if (!user.getEmail().equals(updateRequest.getEmail()) && 
                            userRepository.existsByEmail(updateRequest.getEmail())) {
                            throw new RuntimeException("Email already in use");
                        }
                        user.setEmail(updateRequest.getEmail());
                    }
                    
                    if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
                    }
                    
                    log.info("Updated user: {}", user.getUsername());
                    return UserDTO.Response.fromUser(userRepository.save(user));
                });
    }

    @Transactional
    public boolean toggleUserActive(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(!user.isActive());
                    userRepository.save(user);
                    log.info("Toggled active status for user {}: {}", user.getUsername(), user.isActive());
                    return true;
                })
                .orElse(false);
    }
}