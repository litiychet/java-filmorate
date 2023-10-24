package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
public class Film {
    private int id;
    @NotEmpty
    @NotBlank
    private final String name;
    @NotEmpty
    @NotBlank
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    private final int duration;
}
