package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    int reviewId;
    String content;

    boolean isPositive;
    int userId;
    int filmId;
    int useful;

    public Review(int reviewId, String content, String isPositive, int userId, int filmId, int useful) {
        this.reviewId = reviewId;
        this.content = content;
        if (isPositive == null) {
            throw new ValidationException("isPositive cannot be empty");
        } else this.isPositive = !isPositive.equals("false");
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }


    public boolean getIsPositive() {
        return isPositive;
    }
}

