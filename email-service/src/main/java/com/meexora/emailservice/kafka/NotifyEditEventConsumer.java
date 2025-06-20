package com.meexora.emailservice.kafka;

import com.meexora.common.kafka.AccountVerificationMessage;
import com.meexora.common.kafka.NotifyUsersEventEditedMessage;
import com.meexora.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifyEditEventConsumer {
    private final MailService mailService;

    @KafkaListener(topics = "notify-event-edited", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NotifyUsersEventEditedMessage message) {
        mailService.sendEventUpdatedEmails(message);
    }
}
