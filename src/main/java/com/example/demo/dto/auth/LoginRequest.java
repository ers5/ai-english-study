// src/main/java/com/example/demo/dto/auth/LoginRequest.java
package com.example.demo.dto.auth;

public record LoginRequest(
        String username,
        String password
) {
}
