package com.roman.chat.client.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class ChatUserDto {
    private UUID id;
    private String userName;
}