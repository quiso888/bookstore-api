package com.tuempresa.bookstore.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user of the bookstore who can rate and review books.
 * <p>
 * Relationships:
 * <ul>
 *   <li>User 1:N {@link Calification} — a user can rate several books.</li>
 *   <li>User 1:N {@link Review} — a user can write several reviews.</li>
 * </ul>
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Calification> califications = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    protected User() {
        // Required by JPA
    }

    public User(String username, String email) {
        setUsername(username);
        setEmail(email);
    }

    // ------------------- Business rules -------------------

    /**
     * Rule: username is required and cannot be blank.
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        this.username = username.trim();
    }

    /**
     * Rule: email is required and must contain '@'.
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        String normalized = email.trim().toLowerCase();
        if (!normalized.contains("@")) {
            throw new IllegalArgumentException("Email must be valid");
        }
        this.email = normalized;
    }

    // Package-private: used by Calification / Review to keep both sides in sync
    void addCalificationInternal(Calification calification) {
        califications.add(calification);
    }

    void removeCalificationInternal(Calification calification) {
        califications.remove(calification);
    }

    void addReviewInternal(Review review) {
        reviews.add(review);
    }

    void removeReviewInternal(Review review) {
        reviews.remove(review);
    }

    // ------------------- Getters -------------------

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<Calification> getCalifications() {
        return List.copyOf(califications);
    }

    public List<Review> getReviews() {
        return List.copyOf(reviews);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}
