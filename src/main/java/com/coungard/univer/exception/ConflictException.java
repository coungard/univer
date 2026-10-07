package com.coungard.univer.exception;

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

    public ConflictException(String field, String message) {
        super(message);
        this.field = field;
    }
}
