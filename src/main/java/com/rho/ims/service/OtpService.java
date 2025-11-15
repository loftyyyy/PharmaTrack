package com.rho.ims.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;

    public OtpService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public String generateOtp(String email){
        // Key
        String key = "otp:" + email;

        // Value
        String otp = String.format("%06d", new Random().nextInt(999999));

        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);

        return otp;
    }

    public boolean verifyOtp(String email, String otp){
        String key = "otp:" + email;

        Object value = redisTemplate.opsForValue().get(key);

        if(value == null){
            return false;

        }

        return value.equals(otp);
    }

    public void deleteOtp(String email){
        String key = "otp:" + email;
        redisTemplate.delete(key);
    }

}
