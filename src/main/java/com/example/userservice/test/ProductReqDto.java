package com.example.userservice.test;

import lombok.Builder;
import lombok.Data;

@Data
public class ProductReqDto {
    private String email;
    private long pdtId;

    @Builder
    public ProductReqDto(String email, long ptId) {
        this.email = email;
        this.pdtId = ptId;
    }
}
