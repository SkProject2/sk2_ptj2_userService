package com.example.userservice.service;

import com.example.userservice.dto.LoginDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.jwt.JwtTokenProvider;
import com.example.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenService tokenService;

    public void login(LoginDto loginDto, HttpServletResponse response) throws Exception {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        try{
            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다.") );
            // 내부적으로 password를 똑같이 인코딩해서 비교
            if(!passwordEncoder.matches( password, userEntity.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            String accessToken = jwtTokenProvider.createAccessToken(email, password);
            String refreshToken = tokenService.getRefreshToken(email);
            // 가입 한 적 없으면
            if(refreshToken == null) {
                refreshToken = jwtTokenProvider.createRefreshToken();
                tokenService.saveRefreshToken(email, refreshToken);
            }

            response.addHeader("RefreshToken", refreshToken);
            response.addHeader("AccessToken", accessToken);
            response.addHeader("X-Auth-User", email);
        } catch(Exception e) {
            throw new Exception("로그인 에러");
        }
    }

    public void logout(String email, String accessToken){
        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("부적절한 토큰입니다.");
        }
        tokenService.deleteRefreshToken(email);
    }

    public void deleteUser(String email, String accessToken) {
        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("부적절한 토큰입니다.");
        }
        tokenService.deleteRefreshToken(email);
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        userRepository.delete(userEntity);
    }
}
