package com.tuempresa.bookstore.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the author of one or more books.
 * <p>
 * Relationship: an Author can have written several Books, and a Book can
 * have several Authors (co-authorship) -> N:M relationship, mapped from the
 * owning side in {@link Book#getAuthors()}.
 */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 60)
    private String nationality;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 2000)
    private String biography;

    // Inverse side of the N:M relationship (owning side is Book.authors)
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    protected Author() {
        // Required by JPA
    }

    public Author(String name) {
        setName(name);
    }

    public Author(String name, String nationality, LocalDate birthDate) {
        setName(name);
        setNationality(nationality);
        setBirthDate(birthDate);
    }

    // ------------------- Business rules -------------------

    /**
     * Rule: the author's name is required and cannot be blank.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("The author's name is required");
        }
        this.name = name.trim();
    }

    /**
     * Rule: birth date, if provided, cannot be in the future.
     */
    public void setBirthDate(LocalDate birthDate) {
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        this.birthDate = birthDate;
    }

    public void setNationality(String nationality) {
        this.nationality = (nationality == null) ? null : nationality.trim();
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    // ------------------- Getters -------------------

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBiography() {
        return biography;
    }

    /**
     * Read-only view of the author's books. To modify it, use
     * {@link Book#addAuthor(Author)} / {@link Book#removeAuthor(Author)},
     * which keep both sides of the relationship in sync.
     */
    public Set<Book> getBooks() {
        return Set.copyOf(books);
    }

    // Package-private: used only by Book to keep the bidirectional relationship in sync
    void addBookInternal(Book book) {
        books.add(book);
    }

    void removeBookInternal(Book book) {
        books.remove(book);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return id != null && id.equals(author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Author{id=" + id + ", name='" + name + "'}";
    }
}
