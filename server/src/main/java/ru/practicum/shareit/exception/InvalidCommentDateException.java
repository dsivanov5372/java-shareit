package ru.practicum.shareit.exception;

public class InvalidCommentDateException extends RuntimeException {
    public InvalidCommentDateException(String message) {
        super(message);
    }
}
