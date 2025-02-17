package com.example.userservice.service;

import com.example.userservice.controller.UserController;
import com.example.userservice.dto.*;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaProducer kafkaProducer;

    public void createUser(UserDto userDto) {
        if(userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("정확한 이메일을 입력해주세요.");
        }

        if(userDto.getUserName() == null || userDto.getUserName().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름을 입력해주세요.");
        }

        if(userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        if(userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles("ROLE_USER")
                .enable(false)
                .build();
        userRepository.save(userEntity);
        //sendValidEmail(userEntity);
    }

    private void sendValidEmail(UserEntity userEntity) {
        // 유일하게 식별할 수 있는 토큰 생성 -> 128비트 길이
        String token = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(token, userEntity.getEmail(), 6, TimeUnit.HOURS);

        String url = "http://localhost:8080/user/valid?token=" + token;
        logger.info("메일 보내는 주소: " + userEntity.getEmail() + " " + url);
        sendMail(userEntity.getEmail(), "Email 인증 메일입니다.", "아래의 링크를 눌러서 인증해주세요\n" + url);
    }

    private void sendMail(String email, String subject, String text) {
        logger.info("메일 보내기: " + email + " " + subject + " " + text);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
    }

    public void updateActivate(String token) {
        String email = (String)redisTemplate.opsForValue().get(token);
        // 없다면
        if(email == null) {
            throw new IllegalArgumentException("토큰이 잘 못 되었습니다.");
        }
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자 오류") );
        userEntity.setEnable(true);
        userRepository.save(userEntity);

        redisTemplate.delete(token);

    }

    public UserDetailDto getUserDetail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자 오류") );
        return UserDetailDto.builder()
                .email(userEntity.getEmail())
                .userName(userEntity.getUsername())
                .password(userEntity.getPassword())
                .hp(userEntity.getHp())
                .address(userEntity.getAddress())
                .build();
    }

    public void modifyUser(CheckUserDto checkUserDto, String email) throws JsonProcessingException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자 오류") );
        // 1. 비밀번호 틀리면 안됨
        if(!passwordEncoder.matches( checkUserDto.getPrevPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 2. 이메일 동일한거 존재하면 안됨
        if( !email.equals(checkUserDto.getEmail()) && userRepository.findByEmail(checkUserDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
        // 3. 다 통과하면 입력한대로 유저 정보 업데이트
        userEntity.setEmail(checkUserDto.getEmail());
        if(checkUserDto.getNewPassword().equals("")){
            logger.info("비밀번호 수정 안함");
        }
        else userEntity.setPassword(passwordEncoder.encode(checkUserDto.getNewPassword()));
        userEntity.setHp(checkUserDto.getHp());
        userEntity.setAddress(checkUserDto.getAddress());
        userEntity.setUserName(checkUserDto.getUserName());
        // 3-1. 이메일이 바뀐거면 다른 서비스들에서 이메일 업데이트 해야 함
        if(!email.equals(checkUserDto.getEmail())) {
            try {
                kafkaProducer.sendUserUpdate("user-update", ChangeUserDto.builder()
                        .prevEmail(email)
                        .newEmail(checkUserDto.getEmail())
                        .build());
                // 회원 이메일이 수정되면 리프레시 토큰 일치하지 않으니까 삭제하고
                // 클라이어느트에서 로그아웃 처리 시키기
                tokenService.deleteRefreshToken(email);
            } catch (Exception e){
                throw e;
            }
        }
        // 4. 유저 정보 업데이트
        userRepository.save(userEntity);
    }

    public LoginDataDto getLoginDate(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자 오류"));
        return LoginDataDto.builder()
                .userId(userEntity.getEmail())
                .role(userEntity.getRoles())
                .message("로그인 성공")
                .build();
    }
}
