package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    @Null(groups = Marker.Create.class)
    @NotNull(groups = Marker.Update.class)
    private Long id;
    @NotBlank
    private String name;
    @Size(min = 0, max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Min(1)
    private Integer duration;
    @Singular
    private Set<Long> usersLikes = new HashSet<>();
}