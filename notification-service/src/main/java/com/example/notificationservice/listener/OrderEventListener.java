package com.example.notificationservice.listener;

import com.example.notificationservice.event.OrderPlacedEvent;
import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    @Autowired
    EmailService emailService;

    @KafkaListener(topics = "order-topic", groupId = "notification-group", containerFactory = "orderKafkaListenerContainerFactory")
    public void consume(OrderPlacedEvent event) {
        System.out.println("📨 Nhận được event từ Kafka: " + event);
        try {
            emailService.sendOrderEmail(event);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi mail: " + e.getMessage());
        }
    }
}
