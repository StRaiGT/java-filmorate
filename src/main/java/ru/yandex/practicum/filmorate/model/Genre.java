package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Genre implements Comparable<Genre> {
    private Integer id;
    private String name;

    @Override
    public int compareTo(Genre genre) {
        return this.id - genre.getId();
    }
}
