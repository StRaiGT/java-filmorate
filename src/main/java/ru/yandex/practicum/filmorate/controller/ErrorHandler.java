package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IllegalAddFriendException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotLikeFilmException;
import ru.yandex.practicum.filmorate.exception.UsersNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class, UsersNotFriendsException.class, UserNotLikeFilmException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({FilmAlreadyExistException.class, UserAlreadyExistException.class, IllegalAddFriendException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}