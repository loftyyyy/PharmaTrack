package com.rho.ims.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;

    public OtpService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

        /**
     * Generates a 6-digit one-time password (OTP) for the specified email address,
     * stores it in Redis with a 5-minute expiration, and returns the OTP.
     *
     * @param email the email address for which to generate the OTP
     * @return the generated 6-digit OTP as a String
     *
     * The OTP will expire and be invalid after 5 minutes.
     */

    public String generateOtp(String email){
        // Key
        String key = "otp:" + email;

        // Value
        String otp = String.format("%06d", new SecureRandom().nextInt(1000000));

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
