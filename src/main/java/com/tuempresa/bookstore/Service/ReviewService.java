package com.tuempresa.bookstore.service;

import com.tuempresa.bookstore.model.Book;
import com.tuempresa.bookstore.model.Calification;
import com.tuempresa.bookstore.model.Review;
import com.tuempresa.bookstore.model.User;
import com.tuempresa.bookstore.repository.BookRepository;
import com.tuempresa.bookstore.repository.CalificationRepository;
import com.tuempresa.bookstore.repository.ReviewRepository;
import com.tuempresa.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CalificationRepository calificationRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository,
                         CalificationRepository calificationRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.calificationRepository = calificationRepository;
    }

    /**
     * Creates a review for a book. Optionally links an existing calification.
     * Purchase of the book is not required.
     */
    @Transactional
    public Review createReview(Long userId, Long bookId, String comment, Long calificationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        Review review;
        if (calificationId != null) {
            Calification calification = calificationRepository.findById(calificationId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Calification not found: " + calificationId));
            review = new Review(user, comment, calification);
        } else {
            review = new Review(user, comment);
        }

        book.addReview(review);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> findByBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        return reviewRepository.findByBookId(bookId);
    }

    @Transactional(readOnly = true)
    public List<Review> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return reviewRepository.findByUserId(userId);
    }
}
