package com.example.userservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckUserDto {
    private String email;
    private String userName;
    private String prevPassword;
    private String newPassword;
    private String hp;
    private String address;

    @Builder
    public CheckUserDto(String email, String userName, String prevPassword, String newPassword, String hp, String address) {
        this.email = email;
        this.userName = userName;
        this.prevPassword = prevPassword;
        this.newPassword = newPassword;
        this.hp = hp;
        this.address = address;
    }
}
