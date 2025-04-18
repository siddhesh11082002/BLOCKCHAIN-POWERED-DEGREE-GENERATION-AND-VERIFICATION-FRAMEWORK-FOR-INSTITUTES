package com.example.certificatesystem.model;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UserDTO {
    
    @Data
    public static class CreateRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        private String password;
        
        @NotBlank(message = "Full name is required")
        private String fullName;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
    }
    
    @Data
    public static class Response {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime lastLogin;
        
        public static Response fromUser(User user) {
            Response response = new Response();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setActive(user.isActive());
            response.setCreatedAt(user.getCreatedAt());
            response.setLastLogin(user.getLastLogin());
            return response;
        }
    }
    
    @Data
    public static class UpdateRequest {
        private String fullName;
        
        @Email(message = "Email should be valid")
        private String email;
        
        private String password;
    }
}