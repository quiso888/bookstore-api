package com.tuempresa.bookstore.model;

import jakarta.persistence.*;

import java.util.*;

/**
 * Represents the literary work itself (the "title"), independent of its
 * concrete publications.
 * <p>
 * Relationships:
 * <ul>
 *   <li>Book N:M Author — a book can have one or several authors.</li>
 *   <li>Book 1:N Edition — the same book can have several editions
 *       (reprints, publishers, formats), each with its own ISBN,
 *       publication date, page count and price.</li>
 *   <li>Book 1:N {@link Calification} — users can rate the book from 1 to 5,
 *       whether or not they bought it from us.</li>
 *   <li>Book 1:N {@link Review} — users can write textual reviews of the book.</li>
 * </ul>
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 60)
    private String genre;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Edition> editions = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calification> califications = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    protected Book() {
        // Required by JPA
    }

    public Book(String title) {
        setTitle(title);
    }

    public Book(String title, Author mainAuthor) {
        setTitle(title);
        addAuthor(mainAuthor);
    }

    // ------------------- Business rules -------------------

    /**
     * Rule: the title is required.
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("The book title is required");
        }
        this.title = title.trim();
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Rule: a null author cannot be added, and the same author is never
     * duplicated (the collection is a Set).
     */
    public void addAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        if (authors.add(author)) {
            author.addBookInternal(this);
        }
    }

    /**
     * Rule: a book must keep at least one author; removing the last one
     * is not allowed.
     */
    public void removeAuthor(Author author) {
        if (authors.size() <= 1) {
            throw new IllegalStateException("The book must keep at least one author");
        }
        if (authors.remove(author)) {
            author.removeBookInternal(this);
        }
    }

    /**
     * Rule: every edition belongs to exactly one book; adding it syncs the
     * owning side of the relationship (Edition.book).
     */
    public void addEdition(Edition edition) {
        if (edition == null) {
            throw new IllegalArgumentException("Edition cannot be null");
        }
        edition.assignBook(this);
        editions.add(edition);
    }

    public void removeEdition(Edition edition) {
        if (editions.remove(edition)) {
            edition.assignBook(null);
        }
    }

    /**
     * Rule: every calification belongs to exactly one book; adding it syncs
     * the owning side of the relationship ({@link Calification}'s book).
     * Purchase history is not required.
     */
    public void addCalification(Calification calification) {
        if (calification == null) {
            throw new IllegalArgumentException("Calification cannot be null");
        }
        calification.assignBook(this);
        califications.add(calification);
    }

    public void removeCalification(Calification calification) {
        if (califications.remove(calification)) {
            calification.assignBook(null);
        }
    }

    /**
     * Rule: every review belongs to exactly one book; adding it syncs the
     * owning side of the relationship ({@link Review}'s book).
     * Purchase history is not required.
     */
    public void addReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        review.assignBook(this);
        reviews.add(review);
    }

    public void removeReview(Review review) {
        if (reviews.remove(review)) {
            review.assignBook(null);
        }
    }

    /**
     * Rule: a book is only available for sale if it has at least one
     * registered edition.
     */
    public boolean isAvailable() {
        return !editions.isEmpty();
    }

    /**
     * Returns the average score of all califications, if any exist.
     */
    public Optional<Double> getAverageCalification() {
        if (califications.isEmpty()) {
            return Optional.empty();
        }
        double average = califications.stream()
                .mapToInt(Calification::getScore)
                .average()
                .orElse(0);
        return Optional.of(average);
    }

    /**
     * Returns the most recent edition by publication date, if any exists.
     */
    public Optional<Edition> getMostRecentEdition() {
        return editions.stream()
                .max(Comparator.comparing(Edition::getPublicationDate));
    }

    // ------------------- Getters -------------------

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Set<Author> getAuthors() {
        return Set.copyOf(authors);
    }

    public List<Edition> getEditions() {
        return List.copyOf(editions);
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
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "'}";
    }
}
