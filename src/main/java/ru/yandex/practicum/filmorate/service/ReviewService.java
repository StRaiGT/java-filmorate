package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Optional<Review> createReview(Review review) {
        validateOfReview(review);
        log.info("Добавление отзыва {}", review);
        return reviewStorage.createReview(review);
    }

    public Optional<Review> updateReview(Review review) {
        validateOfReview(review);
        log.info("обновление отзыва {}", review);
        return reviewStorage.updateReview(review);
    }

    public boolean removeReview(int id) {
        log.info("Удаление отзыва с id {}", id);
        return reviewStorage.removeReview(id);
    }

    public Optional<Review> getReviewById(int id) {
        log.info("Возврат отзыва с id {}", id);
        return reviewStorage.getReviewById(id);
    }

    public Collection<Review> getReviews(int filmId, int count) {
        log.info("Возврат всех отзывов");
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLikeReview(int id, int userId) {
        log.info("Добавление лайка отзыву с id {}", id);
        reviewStorage.addLikeReview(id, userId);
    }

    public void addDislikeReview(int id, int userId) {
        log.info("Добавление дизлайка отзыву с id {}", id);
        reviewStorage.addDislikeReview(id, userId);
    }

    public boolean removeLikeReview(int id, int userId) {
        log.info("Удаление лайка отзыву с id {}", id);
        return reviewStorage.removeLikeReview(id, userId);
    }

    public boolean removeDislikeReview(int id, int userId) {
        log.info("Удаление дизлайка отзыву с id {}", id);
        return reviewStorage.removeDislikeReview(id, userId);
    }

    private void validateOfReview(Review review) {
        if (review.getContent() == null) {
            log.warn("Error in context: context cannot be empty");
            throw new ValidationException("Context cannot be empty");
        }
        if (review.getUserId() == 0) {
            log.warn("Error in userId: userId cannot be empty");
            throw new ValidationException("userId cannot be empty");
        }
        if (review.getFilmId() == 0) {
            log.warn("Error in filmId: filmId cannot be empty");
            throw new ValidationException("filmId cannot be empty");
        }
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
    }
}
