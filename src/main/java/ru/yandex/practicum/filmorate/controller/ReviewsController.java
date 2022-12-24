package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewService reviewService;

    @PostMapping
    public Optional<Review> createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Optional<Review> updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("{id}")
    public void removeReview(@PathVariable int id) {
        reviewService.removeReview(id);
    }

    @GetMapping("{id}")
    public Optional<Review> getReviewById(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getReviews(
            @RequestParam(defaultValue = "-1",required = false) int filmId,
            @RequestParam(defaultValue = "10", required = false) int count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLikeReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislikeReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLikeReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeLikeReview(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void removeDislikeReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeDislikeReview(id, userId);
    }

}
