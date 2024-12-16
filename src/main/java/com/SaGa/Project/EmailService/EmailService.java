package com.SaGa.Project.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendConfirmationEmail(String toEmail, String confirmationToken) {
        String confirmationLink = "http://localhost:5173/verificationSuccess?token=" + confirmationToken;
        String subject = "Email Verification";
        String content = "<p>Dear user,</p>"
                + "<p>Please click the link below to verify your registration:</p>"
                + "<a href=\"" + confirmationLink + "\">Verify your email</a>"
                + "<br><p>Thank you!</p>";

        System.out.println(toEmail);
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            System.out.println("Mail Send successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordResetEmail(String email, String token) {

        String resetLink = "http://localhost:8080/user/reset-password?token="+token;
        String subject = "Password Reset Request";
        String content = "<p>Dear User, </p>"
                + "<p>We received a request to reset your password.</p>"
                + "<p>Please click the link below to reset your password:</p>"
                + "<a href=\"" + resetLink + "\">Reset your password</a>"
                + "<br><p>If you did not request this, please ignore this email.</p>";

        sendHtmlEmail(email, subject, content);
    }
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
