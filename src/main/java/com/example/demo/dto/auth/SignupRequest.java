// src/main/java/com/example/demo/dto/auth/SignupRequest.java
package com.example.demo.dto.auth;

public record SignupRequest(
        String username,
        String password,
        String email
) {
}
