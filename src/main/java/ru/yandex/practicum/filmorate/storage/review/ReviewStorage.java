package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> createReview(Review review);

    Optional<Review> getReviewById(int id);

    Collection<Review> getReviews(int filmId, int count);

    Optional<Review> updateReview(Review review);

    void addLikeReview(int id, int userId);

    void addDislikeReview(int id, int userId);

    Boolean removeLikeReview(int id, int userId);

    Boolean removeDislikeReview(int id, int userId);

    Boolean removeReview(int id);
}
