package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name="members")
@Data
@NoArgsConstructor
public class UserEntity implements UserDetails {
    // email -> primary key
    // username, password, hp, address, role
    // enable (인증여부)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    @Column(name="username") // userDetails에 있는 username
    private String userName;

    private String password;
    private String hp;
    private String address;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ROLE_USER'")
    private String roles;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean enable;


    @Builder
    public UserEntity(String email, String userName, String password, String hp, String address, String roles, boolean enable) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.hp = hp;
        this.address = address;
        this.roles = roles;
        this.enable = enable;
    }

    // ---------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역할 설정
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        // email + password로 로그인을 하겠다
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // UserDetails.super.isCredentialsNonExpired();
    }
    @Override
    public boolean isEnabled() {
        return enable;
    }
}
