package com.tuempresa.bookstore.controller;

import com.tuempresa.bookstore.model.Review;
import com.tuempresa.bookstore.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(
            @RequestParam("userId") Long userId,
            @RequestParam("bookId") Long bookId,
            @RequestParam("comment") String comment,
            @RequestParam(name = "calificationId", required = false) Long calificationId) {
        return reviewService.createReview(userId, bookId, comment, calificationId);
    }

    /**
     * Builds and returns the review exactly as it would be created, but
     * without saving it, so the user can preview it before confirming.
     * Uses the same parameters as {@link #createReview}.
     */
    @PostMapping("/preview")
    public Review previewReview(
            @RequestParam("userId") Long userId,
            @RequestParam("bookId") Long bookId,
            @RequestParam("comment") String comment,
            @RequestParam(name = "calificationId", required = false) Long calificationId) {
        return reviewService.previewReview(userId, bookId, comment, calificationId);
    }

    @GetMapping
    public List<Review> list(
            @RequestParam(name = "bookId", required = false) Long bookId,
            @RequestParam(name = "userId", required = false) Long userId) {
        if (bookId != null) {
            return reviewService.findByBook(bookId);
        }
        if (userId != null) {
            return reviewService.findByUser(userId);
        }
        throw new IllegalArgumentException("Provide bookId or userId");
    }
}