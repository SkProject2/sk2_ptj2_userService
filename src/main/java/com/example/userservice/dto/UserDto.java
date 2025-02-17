package com.example.userservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDto {
    private String email;
    private String userName;
    private String password;
    private String hp;

}
