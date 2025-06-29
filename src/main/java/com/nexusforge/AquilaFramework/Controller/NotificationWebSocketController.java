package com.nexusforge.AquilaFramework.Controller;

import com.nexusforge.AquilaFramework.Entity.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class NotificationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String message) {
        NotificationMessage notification = new NotificationMessage();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
