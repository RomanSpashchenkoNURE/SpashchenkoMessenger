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
public class ChatDto {
    private UUID id;
    private UUID ownerUserId;
    private String title;
    private ZonedDateTime startedAt;
}