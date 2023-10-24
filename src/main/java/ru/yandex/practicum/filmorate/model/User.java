package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class User {
    private int id;
    @Email
    @NotBlank
    @NotEmpty
    @NotNull
    private final String email;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String login;
    private String name;
    @NotNull
    private final LocalDate birthday;
}
