package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    Long id;
    String name;
    @Email(message = "Invalid email address!")
    String email;
}