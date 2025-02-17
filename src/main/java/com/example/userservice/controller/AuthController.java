package com.example.userservice.controller;

import com.example.userservice.dto.LoginDataDto;
import com.example.userservice.dto.LoginDto;
import com.example.userservice.dto.SendUserDto;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        try {
            logger.info("로그인 시도: " + loginDto.getEmail());
            authService.login(loginDto, response);
            LoginDataDto loginDataDto = userService.getLoginDate(loginDto.getEmail());
            return ResponseEntity.ok(loginDataDto);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("로그인 실패: " + e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(500).body("서버측 에러" +e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Auth-User") String email, @RequestHeader("Authorization") String accessToken){
        try {
            authService.logout(email, accessToken);
            return ResponseEntity.ok("로그아웃 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("토큰 에러: " + e.getMessage());
        }

    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("X-Auth-User") String email, @RequestHeader("Authorization") String accessToken){
        try {
            authService.deleteUser(email, accessToken);
            try{
                kafkaProducer.sendUserDelete("user-delete", SendUserDto.builder().email(email).build());
            } catch (Exception e){
                return ResponseEntity.status(500).body("카프카 오류" + e.getMessage());
            }
            return ResponseEntity.ok("회원탈퇴 처리 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("토큰 에러: " + e.getMessage());
        }
    }
}
