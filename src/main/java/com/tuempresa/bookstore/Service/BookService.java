package com.tuempresa.bookstore.service;

import com.tuempresa.bookstore.model.Book;
import com.tuempresa.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> simpleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository.searchByTitleOrAuthor(query.trim());
    }

    public List<Book> search(String title, String author, String isbn) {
        String t = normalize(title);
        String a = normalize(author);
        String i = normalize(isbn);

        if (t.isEmpty() && a.isEmpty() && i.isEmpty()) {
            return List.of();
        }
        return bookRepository.search(t, a, i);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}