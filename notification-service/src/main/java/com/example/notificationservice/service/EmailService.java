package com.example.notificationservice.service;

import com.example.notificationservice.event.OrderPlacedEvent;
import com.example.notificationservice.event.UserPlacedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendOrderEmail(OrderPlacedEvent event) {
        try {
            Context context = new Context();
            context.setVariable("userId", event.getUserId());
            context.setVariable("orderId", event.getOrderId());
            context.setVariable("total", event.getTotal());

            String htmlBody = templateEngine.process("order-success", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("hello@demomailtrap.co");
            helper.setTo("tientran13452@gmail.com");
            helper.setSubject("🛒 Đơn hàng mới #" + event.getOrderId());
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("📧 Gửi email thành công!");

        } catch (MessagingException e) {
            System.err.println("❌ Gửi email thất bại: " + e.getMessage());
        }
    }

    public void sendMailRegisterSuccess(UserPlacedEvent event) {
        try {
            Context context = new Context();
            context.setVariable("userId", event.getId());
            context.setVariable("email", event.getEmail());
            context.setVariable("phone", event.getPhone());
            context.setVariable("fullName", event.getFullName());

            String htmlBody = templateEngine.process("user-register-success", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("hello@demomailtrap.co");
            helper.setTo("tientran13452@gmail.com");
            helper.setSubject("🎉 Đăng ký thành công - Chào mừng " + event.getFullName());
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("📧 Gửi email đăng ký thành công đến: " + event.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Gửi email thất bại: " + e.getMessage());
        }
    }

}
