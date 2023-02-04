package ru.practicum.shareit.exception;

public class PageSizeException extends RuntimeException {
    public PageSizeException(String message) {
        super(message);
    }
}