package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Email(message = "Invalid email address!")
    String email;
}