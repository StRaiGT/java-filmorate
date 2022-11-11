package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String Error;

    public ErrorResponse(String error) {
        Error = error;
    }
}
