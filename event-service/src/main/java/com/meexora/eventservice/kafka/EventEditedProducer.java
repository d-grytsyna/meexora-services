package com.meexora.eventservice.kafka;

import com.meexora.common.kafka.EventCreatedMessage;
import com.meexora.common.kafka.EventEditedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventEditedProducer {

    private final KafkaTemplate<String, EventEditedMessage> kafkaTemplate;

    @Value("${kafka.topic.event-edited}")
    private String eventEditedTopic;

    public void sendEventEdited(EventEditedMessage message) {
        kafkaTemplate.send(eventEditedTopic, message);
    }
}
