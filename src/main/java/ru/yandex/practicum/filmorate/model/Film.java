package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
public class Film {
    @Null(groups = Marker.Create.class)
    @NotNull(groups = Marker.Update.class)
    private Long id;
    @NotBlank
    private final String name;
    @Size(min = 0, max = 200)
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    @Min(1)
    private final Integer duration;
}
