package com.tuempresa.bookstore.controller;

import com.tuempresa.bookstore.model.Book;
import com.tuempresa.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public List<Book> simpleSearch(@RequestParam("q") String query) {
        return bookService.simpleSearch(query);
    }
}