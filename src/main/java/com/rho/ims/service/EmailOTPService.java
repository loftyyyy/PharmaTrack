package com.rho.ims.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailOTPService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailOTPService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String otp) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setFrom(sender, "PharmaTrack");

        helper.setSubject("PharmaTrack – Password Reset OTP Code");

        String htmlBody = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; color: #333; line-height: 1.6; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; 
                     border: 1px solid #e2e2e2; border-radius: 8px; }
        .header { font-size: 20px; font-weight: bold; margin-bottom: 20px; color: #1a73e8; }
        .otp { font-size: 28px; font-weight: bold; color: #000; background-color: #f2f2f2;
               padding: 10px; text-align: center; border-radius: 5px; margin: 20px 0;
               letter-spacing: 3px; }
        .footer { font-size: 12px; color: #888; margin-top: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">Password Reset Request</div>

        <p>Hello,</p>
        <p>We received a request to reset the password for your <strong>PharmaTrack</strong> account.</p>

        <p>Please use the one-time password (OTP) below to reset your password.
        This OTP is valid for <strong>5 minutes</strong>.</p>

        <div class="otp">${otp}</div>

        <p>If you did not request a password reset, simply ignore this email.</p>

        <div class="footer">
            &copy; 2025 PharmaTrack. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        helper.setText(htmlBody.replace("${otp}", otp), true);

        mailSender.send(message);

    }
}
