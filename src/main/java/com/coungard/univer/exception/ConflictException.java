package com.coungard.univer.exception;

import java.util.UUID;
import lombok.Getter;

/**
 * Значение уже занято (например, email или логин при регистрации) — отдаётся клиенту как 409.
 */
@Getter
public class ConflictException extends RuntimeException {

    /**
     * Имя поля запроса, значение которого занято; {@code null}, если поле определить не удалось.
     */
    private final String field;

    /**
     * ID уже существующей записи с этим значением, если клиенту есть смысл предложить выбрать её
     * (например, группу с таким же названием); иначе {@code null}.
     */
    private final UUID existingId;

    public ConflictException(String field, String message) {
        this(field, message, null);
    }

    public ConflictException(String field, String message, UUID existingId) {
        super(message);
        this.field = field;
        this.existingId = existingId;
    }
}
