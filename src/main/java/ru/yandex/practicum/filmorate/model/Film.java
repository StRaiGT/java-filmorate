package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@Builder
public class Film {
    private int id;

    @NotBlank
    private String name;

    @Size(max=200)
    private String description;

    @DateTimeFormat(pattern = "YYYY-mm-dd")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private final Mpa mpa;
    private final Set<Genre> genres = new TreeSet<>();
    private final Set<Director> directors = new TreeSet<>();
}
