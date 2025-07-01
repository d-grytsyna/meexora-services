package com.meexora.bookingservice.kafka;

import com.meexora.common.kafka.NotifyUsersEventEditedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class EditEventEmailProducer {

    private final KafkaTemplate<String, NotifyUsersEventEditedMessage> kafkaTemplate;

    @Value("${kafka.topic.notify-event-edited}")
    private String notifyEventEditedTopic;

    public void sendNotifyEventEdited(NotifyUsersEventEditedMessage message) {
        kafkaTemplate.send(notifyEventEditedTopic, message);
    }
}
