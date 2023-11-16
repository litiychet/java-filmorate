package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @Null(groups = Marker.Create.class)
    @NotNull(groups = Marker.Update.class)
    private Long id;
    @Email
    @NotEmpty
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @Past
    private LocalDate birthday;
    @Singular
    private Set<Long> friendsList = new HashSet<>();
}
