package com.tuempresa.bookstore.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a user's numeric rating of a {@link Book}, from 1 (bad) to 5 (good).
 * <p>
 * Relationships:
 * <ul>
 *   <li>Calification N:1 Book — each calification evaluates exactly one book.</li>
 *   <li>Calification N:1 User — each calification is given by exactly one user.</li>
 * </ul>
 * A user may rate a book even if they did not buy it from this bookstore.
 * A user may give at most one calification per book.
 */
@Entity
@Table(
        name = "califications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_calification_user_book",
                columnNames = {"user_id", "book_id"}
        )
)
public class Calification {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score;

    protected Calification() {
        // Required by JPA
    }

    /**
     * Creates a calification. Attach it to a book with {@link Book#addCalification(Calification)}.
     */
    public Calification(User user, Integer score) {
        setScore(score);
        assignUser(user);
    }

    // ------------------- Business rules -------------------

    /**
     * Rule: score is required and must be between 1 (bad) and 5 (good).
     */
    public void setScore(Integer score) {
        if (score == null) {
            throw new IllegalArgumentException("Score is required");
        }
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(
                    "Score must be between " + MIN_SCORE + " (bad) and " + MAX_SCORE + " (good)");
        }
        this.score = score;
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
            this.user.removeCalificationInternal(this);
        }
        this.user = user;
        user.addCalificationInternal(this);
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

    public Integer getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Calification)) return false;
        Calification that = (Calification) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Calification{id=" + id + ", score=" + score + "}";
    }
}
