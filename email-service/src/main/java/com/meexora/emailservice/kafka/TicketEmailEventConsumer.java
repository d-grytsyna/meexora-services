package com.meexora.emailservice.kafka;

import com.meexora.common.kafka.TicketEmailMessage;
import com.meexora.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketEmailEventConsumer {
    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.ticket-email}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TicketEmailMessage message) {

        mailService.sendTickets(message);
    }
}