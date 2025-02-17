package com.example.userservice.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDetailDto {
    private String email;
    private String userName;
    private String password;
    private String hp;
    private String address;

    @Builder
    public UserDetailDto(String email, String userName, String password, String hp, String address) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.hp = hp;
        this.address = address;
    }
}
