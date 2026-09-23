package com.tuempresa.bookstore.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a user's written review of a {@link Book}.
 * <p>
 * Relationships:
 * <ul>
 *   <li>Review N:1 Book — each review refers to exactly one book.</li>
 *   <li>Review N:1 User — each review is written by exactly one user.</li>
 *   <li>Review N:1 {@link Calification} (optional) — a review may be linked
 *       to the numeric score the user gave that book.</li>
 * </ul>
 * A user may review a book even if they did not buy it from this bookstore.
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calification_id")
    private Calification calification;

    @Column(name = "comment_text", nullable = false, length = 2000)
    private String comment;

    protected Review() {
        // Required by JPA
    }

    /**
     * Creates a review. Attach it to a book with {@link Book#addReview(Review)}.
     */
    public Review(User user, String comment) {
        setComment(comment);
        assignUser(user);
    }

    /**
     * Creates a review linked to an existing calification.
     * Attach it to a book with {@link Book#addReview(Review)}.
     */
    public Review(User user, String comment, Calification calification) {
        this(user, comment);
        setCalification(calification);
    }

    // ------------------- Business rules -------------------

    /**
     * Rule: comment is required and cannot be blank.
     */
    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment is required");
        }
        this.comment = comment.trim();
    }

    /**
     * Rule: if a calification is linked, it must belong to the same user
     * (and to the same book once the review has been attached to one).
     */
    public void setCalification(Calification calification) {
        if (calification != null) {
            if (user != null && calification.getUser() != user) {
                throw new IllegalArgumentException(
                        "Calification must belong to the same user as the review");
            }
            if (book != null && calification.getBook() != null && calification.getBook() != book) {
                throw new IllegalArgumentException(
                        "Calification must belong to the same book as the review");
            }
        }
        this.calification = calification;
    }

    /**
     * Package-private: only {@link Book} should change the book side of the
     * relationship, to keep both sides in sync.
     */
    void assignBook(Book book) {
        this.book = book;
    }

    /**
     * Package-private: keeps both sides of the User relationship in sync.
     */
    void assignUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (this.user != null) {
            this.user.removeReviewInternal(this);
        }
        this.user = user;
        user.addReviewInternal(this);
    }

    // ------------------- Getters -------------------

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public Calification getCalification() {
        return calification;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Review{id=" + id + ", comment='" + comment + "'}";
    }
}