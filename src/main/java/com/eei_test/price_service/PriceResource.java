package com.eei_test.price_service;

import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class PriceResource {

    org.slf4j.Logger logger = LoggerFactory.getLogger(PriceResource.class);

    @Value("${remote.service1}")
    private String remoteEndPoint1;
    @Value("${remote.service2}")
    private String remoteEndPoint2;

    @Autowired
    private MyItemRepository itemRepo;

    private RestTemplate restTemplate;

    public PriceResource(){
        restTemplate = new RestTemplate();
    }

    @GetMapping("/price")
    public PriceResponseEntity priceImpl(@RequestParam("jwt") String jwt) {
        logger.info("/api/price called");

        // get key from remote service
        RemoteRestPKeyEntity result = restTemplate.getForObject(remoteEndPoint1 + "/pkey", RemoteRestPKeyEntity.class);

        // vlidate JWT
        String pkey = result.pkey;
        String[] jwt_pieces = jwt.split("\\.");
        
        //jwt header
        String b64header = jwt_pieces[0]; // not used but for test purpose decoding also header
        String jsonHeader = new String(Base64.getUrlDecoder().decode(b64header));
        //jwt body
        String b64payload = jwt_pieces[1];
        String jsonString = new String(Base64.getUrlDecoder().decode(b64payload));
        //jwt signature
        String b64Signature = jwt_pieces[2];
        try{
            if(!b64Signature.equals(calculateHMAC(b64header+"."+b64payload, pkey))){
                logger.info("jwt failed validation");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT not valid");
            }
        }
        catch(Exception e){
            logger.info(e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT signature validation failed");
        }
        //=== END  validate jwt====

        //req payload manual json to java mapping
        PriceRequestEntity reqEntity=null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            reqEntity = objectMapper.readValue(jsonString, PriceRequestEntity.class);
        }
        catch(Exception e){
            logger.info("json demarshal exception: %s",e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT JSON demarshal failed");
        }

        // remote request for discount -- standard pooled, sync req .. could be made as async in future

        RemoteRestDiscEntity result2 = restTemplate.getForObject(remoteEndPoint2 + "/discount?quantity=" + reqEntity.quantity, RemoteRestDiscEntity.class);

        float myDisk = result2.discount;
        // DB query for price, fetching all data row
        
        Optional<MyItemEntity> item = itemRepo.findById(reqEntity.item);

        if (item.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }    
        
        MyItemEntity mi = item.get();

        //build response entity
        PriceResponseEntity toRet = new PriceResponseEntity();
        toRet.tot_price=((float)mi.price-myDisk*mi.price/100)*reqEntity.quantity;
        if(reqEntity.vat_incl) toRet.tot_price=toRet.tot_price*(float)1.2;
        toRet.quantity=reqEntity.quantity;
        return toRet;
    }

    @GetMapping("/hello")
    public String helloWorld() {
        logger.info("Hello W called");
        return "Hello world from Service RNZ!";
    }
    @GetMapping("/remote-pkey")
    public String remoteKPey() {
        return restTemplate.getForObject(remoteEndPoint1 + "/pkey", String.class);
    }

    @GetMapping("/remote-discount")
    public String remoteDiscount() {
        return restTemplate.getForObject(remoteEndPoint2 + "/discount?quantity=66", String.class);
    }

    private String calculateHMAC(String data, String key) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        String toRet=Base64.getUrlEncoder().encodeToString(hmacBytes);
        //remove padding and return
        return toRet.substring(0,toRet.indexOf("="));
    }
}
