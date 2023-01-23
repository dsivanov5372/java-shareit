package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyRegisteredException(final UserAlreadyRegisteredException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = { InvalidItemRequestException.class, 
                                InvalidEmailException.class,
                                EmptyCommentException.class,
                                InvalidCommentDateException.class,
                                BookingException.class,
                                InvalidStateException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidEmailException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = { UserNotFoundException.class, 
                                BookingNotFoundException.class,
                                ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}