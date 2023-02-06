package ru.practicum.shareit.exception;

public class InvalidItemRequestException extends RuntimeException {
    public InvalidItemRequestException(String message) {
        super(message);
    }
}
