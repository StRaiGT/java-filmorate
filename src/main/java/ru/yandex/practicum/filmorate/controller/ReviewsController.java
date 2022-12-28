package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("{id}")
    public Boolean removeReview(@PathVariable int id) {
        return reviewService.removeReview(id);
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getReviews(
            @RequestParam(defaultValue = "-1",required = false) int filmId,
            @RequestParam(defaultValue = "10", required = false) int count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public Boolean addLikeReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addLikeReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public Boolean addDislikeReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Boolean removeLikeReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.removeLikeReview(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public Boolean removeDislikeReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.removeDislikeReview(id, userId);
    }

}
