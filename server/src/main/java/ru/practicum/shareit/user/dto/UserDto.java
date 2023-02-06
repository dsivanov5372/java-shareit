package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    Long id;
    String name;
    @Email(message = "Invalid email address!")
    String email;
}