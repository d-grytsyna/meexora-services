package com.meexora.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyUsersEventEditedMessage {
    List<String> userEmails;
    private Boolean locationChanged;
    private String location;
    private Boolean dateTimeChanged;
    private String dateTime;
}
