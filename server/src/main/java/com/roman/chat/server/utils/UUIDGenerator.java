package com.roman.chat.server.utils;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

@GenericGenerator(
    name = "UUID",
    type = UUIDGenerator.class
)
public class UUIDGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return UUID.randomUUID();
    }
}