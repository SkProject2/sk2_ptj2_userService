package com.example.userservice.test;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SendPdtDto {
    private String email;
    private long pdtId;
    // 이름, 가격,
    private String pdtName;

    private Float price;

    private String imageUrl;

    @Builder
    public SendPdtDto(String email, long ptId, String pdtName, Float price, String imageUrl) {
        this.email = email;
        this.pdtId = ptId;
        this.pdtName = pdtName;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
