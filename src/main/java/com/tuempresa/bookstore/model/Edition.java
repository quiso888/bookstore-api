package com.tuempresa.bookstore.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a concrete publication of a {@link Book}: its ISBN,
 * publication date, page count, price and other data that vary between
 * editions (for example, a reprint or a digital version of the same book
 * have different ISBN, price and page count).
 * <p>
 * Relationship: Edition N:1 Book — each edition belongs to exactly one book.
 */
@Entity
@Table(name = "editions")
public class Edition {

    // ISBN-10 (9 digits + check digit/X) or ISBN-13 (13 digits), no hyphens
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(?:\\d{9}[\\dX]|\\d{13})$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, unique = true, length = 17)
    private String isbn;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 100)
    private String publisher;

    @Column(name = "edition_number")
    private Integer editionNumber;

    protected Edition() {
        // Required by JPA
    }

    public Edition(String isbn, LocalDate publicationDate, Integer pageCount, BigDecimal price) {
        setIsbn(isbn);
        setPublicationDate(publicationDate);
        setPageCount(pageCount);
        setPrice(price);
    }

    // ------------------- Business rules -------------------

    /**
     * Rule: ISBN is required and must match ISBN-10 or ISBN-13 format
     * (hyphens/spaces are accepted as input but normalized on save).
     */
    public void setIsbn(String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("ISBN is required");
        }
        String cleaned = isbn.replaceAll("[- ]", "").toUpperCase();
        if (!ISBN_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("ISBN must be a valid ISBN-10 or ISBN-13");
        }
        this.isbn = cleaned;
    }

    /**
     * Rule: publication date is required and cannot be in the future.
     */
    public void setPublicationDate(LocalDate publicationDate) {
        if (publicationDate == null) {
            throw new IllegalArgumentException("Publication date is required");
        }
        if (publicationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Publication date cannot be in the future");
        }
        this.publicationDate = publicationDate;
    }

    /**
     * Rule: page count must be greater than zero.
     */
    public void setPageCount(Integer pageCount) {
        if (pageCount == null || pageCount <= 0) {
            throw new IllegalArgumentException("Page count must be greater than zero");
        }
        this.pageCount = pageCount;
    }

    /**
     * Rule: price must be greater than zero.
     */
    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = price;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Rule: if provided, the edition number must be positive (1 = first edition).
     */
    public void setEditionNumber(Integer editionNumber) {
        if (editionNumber != null && editionNumber <= 0) {
            throw new IllegalArgumentException("Edition number must be greater than zero");
        }
        this.editionNumber = editionNumber;
    }

    /**
     * Package-private: only {@link Book} should change the owning side of
     * the relationship, to keep both sides in sync.
     */
    void assignBook(Book book) {
        this.book = book;
    }

    // ------------------- Getters -------------------

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public String getIsbn() {
        return isbn;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getPublisher() {
        return publisher;
    }

    public Integer getEditionNumber() {
        return editionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edition)) return false;
        Edition edition = (Edition) o;
        return id != null && id.equals(edition.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Edition{id=" + id + ", isbn='" + isbn + "', price=" + price + "}";
    }
}
