package com.example.userservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginDataDto {
    private String userId ;
    private String role;
    private String message;

    @Builder
    public LoginDataDto(String userId, String role, String message) {
        this.userId = userId;
        this.role = role;
        this.message = message;
    }
}
