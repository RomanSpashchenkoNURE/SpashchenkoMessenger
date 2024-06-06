package com.roman.chat.client.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatMessageDto {
    private UUID id;
    private UUID chatId;
    private UUID userId;
    private String content;
    private ZonedDateTime createAt;
}