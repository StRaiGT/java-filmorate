package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.enums.Operation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    public Optional<Review> createReview(Review review) {
        validateOfReview(review);
        log.info("Добавление отзыва {}", review);
        Optional<Review> result = reviewStorage.createReview(review);
        feedService.add(result.get().getReviewId(), result.get().getUserId(), REVIEW, ADD);
        return result;
    }

    public Optional<Review> updateReview(Review review) {
        validateOfReview(review);
        log.info("обновление отзыва {}", review);
        feedService.add(review.getReviewId(), reviewStorage.getReviewById(review.getReviewId()).get().getUserId(), REVIEW, UPDATE);
        return reviewStorage.updateReview(review);
    }

    public Boolean removeReview(int id) {
        log.info("Удаление отзыва с id {}", id);
        Review rw = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        feedService.add(rw.getReviewId(), rw.getUserId(), REVIEW, REMOVE);
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

    public Boolean addLikeReview(int id, int userId) {
        log.info("Добавление лайка отзыву с id {}", id);
        return reviewStorage.addLikeReview(id, userId);
    }

    public Boolean addDislikeReview(int id, int userId) {
        log.info("Добавление дизлайка отзыву с id {}", id);
        return reviewStorage.addDislikeReview(id, userId);
    }

    public Boolean removeLikeReview(int id, int userId) {
        log.info("Удаление лайка отзыву с id {}", id);
        return reviewStorage.removeLikeReview(id, userId);
    }

    public Boolean removeDislikeReview(int id, int userId) {
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