package com.tuempresa.bookstore.repository;

import com.tuempresa.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchByTitleOrAuthor(@Param("query") String query);


    @Query("SELECT b FROM Book b WHERE " +
            "(:title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:isbn = '' OR b.isbn = :isbn)")
    List<Book> search(@Param("title") String title,
                      @Param("author") String author,
                      @Param("isbn") String isbn);
}