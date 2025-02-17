package com.example.userservice.controller;

import com.example.userservice.dto.CheckUserDto;
import com.example.userservice.dto.SendUserDto;
import com.example.userservice.dto.UserDetailDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.service.UserService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        logger.info( "회원가입 요청: " + userDto.toString());
        // 회원가입
        try {
            userService.createUser(userDto);
            try{
                kafkaProducer.sendSignup("user-signup", SendUserDto.builder().email(userDto.getEmail()).build());
            } catch (Exception e){
                return ResponseEntity.status(500).body("카프카 오류" + e.getMessage());
            }
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("회원가입 실패" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 에러" + e.getMessage());
        }
    }

    @GetMapping("/valid")
    public ResponseEntity<?> valid(@RequestParam String token) {
        try{
             userService.updateActivate(token);
             return ResponseEntity.ok("이메일 인증 완료, 계정이 활성화 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("토큰이 잘못되었습니다. 다시 인증해주세요" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 오류: " + e.getMessage());
        }
    }


    @GetMapping("/modify")
    public ResponseEntity<?> modify(@RequestHeader("X-Auth-User") String email) {
        try{
            UserDetailDto userDetailDto = userService.getUserDetail(email);
            return ResponseEntity.ok(Map.of("user", userDetailDto));
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(400).body("유저 정보가 없습니다." + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 오류: " + e.getMessage());
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody CheckUserDto checkUserDto, @RequestHeader("X-Auth-User") String email) {
        // 비밀번호가 일치하지 않는다면
        try {
            userService.modifyUser(checkUserDto, email);
            return ResponseEntity.ok("개인정보 수정 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 측 오류: " + e.getMessage());
        }

    }

    /**
     * 개인정보 수정
     * -> 이름 이메일, 비빌번호, 폰번호, 주소 들어오면 다 받아와서 업데이트
     *
     * 비밀번호 검증 -> 비밀번호랑 새 비밀번호랑 들어오면
     *
     * 먼저 비밀번호 검증을 해서 기존 비밀번호랑 같지 않으면 비밀번호가 일치하지 않습니다. 메시지 출력
     *
     * 비밀번호가 같으면 새로운 비밀번호를 암호화해서 데이터베이스에 저장
     */
}
