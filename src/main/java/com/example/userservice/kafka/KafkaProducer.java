package com.example.userservice.kafka;

import com.example.userservice.dto.ChangeUserDto;
import com.example.userservice.dto.SendUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendSignup(String topic, SendUserDto sendUserDto) throws JsonProcessingException {
        logger.info("카프카 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(sendUserDto));
        logger.info("카프카 메세지 전송 완료");
    }

    public void sendUserDelete(String topic, SendUserDto sendUserDto) throws JsonProcessingException {
        logger.info("회원 탈퇴 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(sendUserDto));
        logger.info("회원 탈퇴 메세지 전송 완료");
    }

    public void sendUserUpdate(String topic, ChangeUserDto changeUserDto) throws JsonProcessingException {
        logger.info("회원 정보 수정 메세지 전송");
        try{
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(changeUserDto));
        } catch (Exception e) { throw e; }
        logger.info("회원 정보 수정 메세지 전송 완료");
    }
}
