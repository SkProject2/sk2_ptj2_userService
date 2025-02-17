package com.example.userservice.test;

import com.example.userservice.dto.SendUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestKafProducer {
    private static final Logger logger = LoggerFactory.getLogger(TestKafProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void createPdt(String topic, SendPdtDto sendPdtDto) throws JsonProcessingException {
        logger.info("상품 등록 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(sendPdtDto));
        logger.info("상품 등록 메세지 전송 완료");
    }

    public void deletePdt(String topic, ProductReqDto productReqDto) throws JsonProcessingException {
        logger.info("상품 삭제 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productReqDto));
        logger.info("상품 삭제 메세지 전송 완료");
    }

    public void wishPdt(String topic, ProductReqDto productReqDto) throws JsonProcessingException {
        logger.info("찜 등록 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productReqDto));
        logger.info("찜 등록 메세지 전송 완료");
    }

    public void wishPdtDelete(String topic, ProductReqDto productReqDto) throws JsonProcessingException {
        logger.info("찜 삭제 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productReqDto));
        logger.info("찜 삭제 메세지 전송 완료");
    }

    public void pdtPurchase(String topic, ProductReqDto productReqDto) throws JsonProcessingException {
        logger.info("물품 구매 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productReqDto));
        logger.info("물품 구매 메세지 전송 완료");
    }
}
