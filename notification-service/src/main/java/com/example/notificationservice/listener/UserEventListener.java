package com.example.notificationservice.listener;

import com.example.notificationservice.event.UserPlacedEvent;
import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventListener {
    @Autowired
     EmailService emailService;

    @KafkaListener(topics = "user-topic", groupId = "notification-group", containerFactory = "userKafkaListenerContainerFactory")
    public void handleUserCreated(UserPlacedEvent event) {
        try {
            System.out.println("📥 Nhận được user mới từ Kafka:");
            System.out.println("👤 ID: " + event.getId());
            System.out.println("📧 Email: " + event.getEmail());
            emailService.sendMailRegisterSuccess(event);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi mail: " + e.getMessage());

        }
    }
}
