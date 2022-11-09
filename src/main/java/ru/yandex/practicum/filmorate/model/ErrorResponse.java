package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private final String Error;

    public ErrorResponse(String error) {
        Error = error;
    }

    public String getError() {
        return Error;
    }
}
