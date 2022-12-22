package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class Director implements Comparable<Director>{
    private Integer id;

    @NotBlank
    private String name;

    @Override
    public int compareTo(Director o) {
        return this.id - o.getId();
    }
}
