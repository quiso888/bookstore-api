package com.tuempresa.bookstore.controller;

import com.tuempresa.bookstore.model.Calification;
import com.tuempresa.bookstore.service.CalificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/califications")
public class CalificationController {

    private final CalificationService calificationService;

    @Autowired
    public CalificationController(CalificationService calificationService) {
        this.calificationService = calificationService;
    }

    @PostMapping
    public Calification rateBook(
            @RequestParam("userId") Long userId,
            @RequestParam("bookId") Long bookId,
            @RequestParam("score") Integer score) {
        return calificationService.rateBook(userId, bookId, score);
    }

    @GetMapping
    public List<Calification> list(
            @RequestParam(name = "bookId", required = false) Long bookId,
            @RequestParam(name = "userId", required = false) Long userId) {
        if (bookId != null) {
            return calificationService.findByBook(bookId);
        }
        if (userId != null) {
            return calificationService.findByUser(userId);
        }
        throw new IllegalArgumentException("Provide bookId or userId");
    }

    @GetMapping("/average")
    public Double averageScore(@RequestParam("bookId") Long bookId) {
        return calificationService.getAverageScore(bookId).orElse(null);
    }
}
