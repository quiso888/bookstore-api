package com.tuempresa.bookstore.Service;

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
        Review review = buildReview(userId, bookId, comment, calificationId);
        return reviewRepository.save(review);
    }

    /**
     * Builds and returns a review exactly as {@link #createReview} would,
     * running the same lookups and business validations, but WITHOUT
     * persisting it. Lets the user preview how the review will look before
     * confirming it.
     * <p>
     * The transaction is marked {@code readOnly = true} on purpose: Spring's
     * Hibernate integration switches the persistence context to manual flush
     * mode for read-only transactions, so even though {@code book.addReview}
     * attaches the in-memory review to the (cascading) Book.reviews
     * collection, Hibernate never flushes that change to the database and
     * nothing is written. Only {@link #createReview} actually saves.
     */
    @Transactional(readOnly = true)
    public Review previewReview(Long userId, Long bookId, String comment, Long calificationId) {
        return buildReview(userId, bookId, comment, calificationId);
    }

    /**
     * Loads the user, book and (optional) calification, and assembles a
     * fully linked, in-memory {@link Review}. Does not touch the repository.
     */
    private Review buildReview(Long userId, Long bookId, String comment, Long calificationId) {
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
        return review;
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