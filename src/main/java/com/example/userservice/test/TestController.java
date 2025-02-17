package com.example.userservice.test;

import com.example.userservice.dto.SendUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestKafProducer testKafProducer;

    @PostMapping("/createPdt")
    public String createPdt(@RequestBody SendPdtDto sendPdtDto) {
        try{
            testKafProducer.createPdt( "pdt-create", sendPdtDto);
        } catch (Exception e){
            return "no";
        }
        return "ok";
    }

    @PostMapping("/deletePdt")
    public String deletePdt(@RequestBody ProductReqDto productReqDto) {
        try{
            testKafProducer.deletePdt( "pdt-delete", productReqDto);
        } catch (Exception e){
            return "no";
        }
        return "ok";
    }

    @PostMapping("/createWish")
    public String createWish(@RequestBody ProductReqDto productReqDto) {
        try{
            testKafProducer.wishPdt( "wish-pdt", productReqDto);
        } catch (Exception e){
            return "no";
        }
        return "ok";
    }

    @PostMapping("/deleteWish")
    public String deleteWish(@RequestBody ProductReqDto productReqDto) {
        try{
            testKafProducer.wishPdtDelete( "wish-pdt-delete", productReqDto);
        } catch (Exception e){
            return "no";
        }
        return "ok";
    }

    @PostMapping("/pdtPurchase")
    public String pdtPurchase(@RequestBody ProductReqDto productReqDto) {
        try{
            testKafProducer.pdtPurchase( "pdt-purchase", productReqDto);
        } catch (Exception e){
            return "no";
        }
        return "ok";
    }

}
