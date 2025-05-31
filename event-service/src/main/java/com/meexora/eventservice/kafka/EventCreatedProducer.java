package com.meexora.eventservice.kafka;


import com.meexora.common.kafka.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCreatedProducer {

    private final KafkaTemplate<String, EventCreatedMessage> kafkaTemplate;

    @Value("${kafka.topic.event-created}")
    private String eventCreatedTopic;

    public void sendEventCreated(EventCreatedMessage message) {
        kafkaTemplate.send(eventCreatedTopic, message);
    }
}