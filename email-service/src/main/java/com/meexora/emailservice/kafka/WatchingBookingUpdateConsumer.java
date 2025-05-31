package com.meexora.emailservice.kafka;

import com.meexora.common.kafka.TicketGenerationMessage;
import com.meexora.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchingBookingUpdateConsumer {
    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.watching-booking-update}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TicketGenerationMessage message) {
        mailService.sendWatchingUpdate(message);
    }
}
