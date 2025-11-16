package com.rho.ims.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailOTPService {

    private final JavaMailSender javaMailSender;
    private final JavaMailSenderImpl mailSender;

    public EmailOTPService(JavaMailSender javaMailSender, JavaMailSenderImpl mailSender){
        this.javaMailSender = javaMailSender;
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String otp) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);

        String subject = "PharmaTrack - Password Reset Request – OTP Code";

        String htmlBody = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; color: #333; line-height: 1.6; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e2e2; border-radius: 8px; }
        .header { font-size: 20px; font-weight: bold; margin-bottom: 20px; color: #1a73e8; }
        .otp { font-size: 28px; font-weight: bold; color: #000; background-color: #f2f2f2; padding: 10px; text-align: center; border-radius: 5px; margin: 20px 0; letter-spacing: 3px; }
        .footer { font-size: 12px; color: #888; margin-top: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">Password Reset Request</div>

        <p>Hello,</p>
        <p>We received a request to reset the password for your <strong>PharmaTrack</strong> account.</p>

        <p>Please use the one-time password (OTP) below to reset your password. This OTP is valid for <strong>5 minutes</strong>.</p>

        <div class="otp">123456</div> <!-- Replace 123456 dynamically -->

        <p>If you did not request a password reset, please ignore this email. Your account will remain secure.</p>

        <div class="footer">
            &copy; 2025 YourCompany. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        helper.setSubject(subject);
        helper.setText(htmlBody.replace("123456", otp),true);

        helper.setFrom("beyondsagi@gmail.com");
        javaMailSender.send(message);
    }
}
