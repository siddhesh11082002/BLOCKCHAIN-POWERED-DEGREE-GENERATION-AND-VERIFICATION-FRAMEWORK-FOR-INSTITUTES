package com.example.certificatesystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DebugController {
    
    @GetMapping("/debug")
    public Map<String, String> debug() {
        return Map.of(
            "status", "UP",
            "message", "Debug endpoint is functioning correctly"
        );
    }
}