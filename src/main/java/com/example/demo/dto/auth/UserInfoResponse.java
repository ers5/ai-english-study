// src/main/java/com/example/demo/dto/auth/UserInfoResponse.java
package com.example.demo.dto.auth;

public record UserInfoResponse(
        Long id,
        String username,
        String email
) {
}
