package com.example.notificationservice.event;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderPlacedEvent {
    String orderId;
    String userId;
    Double total;
}
