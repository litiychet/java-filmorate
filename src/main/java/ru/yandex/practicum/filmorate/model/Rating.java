package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
public class Rating {
    @Null(groups = Marker.Create.class)
    @NotNull(groups = Marker.Update.class)
    private Long id;
    @NotBlank
    private String name;
}
