package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.enums.Operation.REMOVE;
import static ru.yandex.practicum.filmorate.enums.Operation.UPDATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    public Review createReview(Review review) {
        log.info("Добавление отзыва {}", review);
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());

        Review result = reviewStorage.createReview(review).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        feedService.add(result.getReviewId(), result.getUserId(), REVIEW, ADD);
        return result;
    }

    public Review updateReview(Review review) {
        log.info("обновление отзыва {}", review);
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());

        feedService.add(review.getReviewId(),
                getReviewById(review.getReviewId()).getUserId(), REVIEW, UPDATE);
        return reviewStorage.updateReview(review).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public Boolean removeReview(int id) {
        log.info("Удаление отзыва с id {}", id);
        Review reviewFromController = getReviewById(id);
        feedService.add(reviewFromController.getReviewId(), reviewFromController.getUserId(), REVIEW, REMOVE);
        return reviewStorage.removeReview(id);
    }

    public Review getReviewById(int id) {
        log.info("Возврат отзыва с id {}", id);
        return reviewStorage.getReviewById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
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
}