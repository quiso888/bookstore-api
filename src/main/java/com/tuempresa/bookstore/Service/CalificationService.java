package com.tuempresa.bookstore.service;

import com.tuempresa.bookstore.model.Book;
import com.tuempresa.bookstore.model.Calification;
import com.tuempresa.bookstore.model.User;
import com.tuempresa.bookstore.repository.BookRepository;
import com.tuempresa.bookstore.repository.CalificationRepository;
import com.tuempresa.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CalificationService {

    private final CalificationRepository calificationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public CalificationService(CalificationRepository calificationRepository,
                               BookRepository bookRepository,
                               UserRepository userRepository) {
        this.calificationRepository = calificationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates or updates a user's calification for a book.
     * Purchase of the book is not required.
     */
    @Transactional
    public Calification rateBook(Long userId, Long bookId, Integer score) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        Optional<Calification> existing = calificationRepository.findByUserIdAndBookId(userId, bookId);
        if (existing.isPresent()) {
            Calification calification = existing.get();
            calification.setScore(score);
            return calificationRepository.save(calification);
        }

        Calification calification = new Calification(user, score);
        book.addCalification(calification);
        return calificationRepository.save(calification);
    }

    @Transactional(readOnly = true)
    public List<Calification> findByBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        return calificationRepository.findByBookId(bookId);
    }

    @Transactional(readOnly = true)
    public List<Calification> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return calificationRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Double> getAverageScore(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        List<Calification> califications = calificationRepository.findByBookId(bookId);
        if (califications.isEmpty()) {
            return Optional.empty();
        }
        double average = califications.stream()
                .mapToInt(Calification::getScore)
                .average()
                .orElse(0);
        return Optional.of(average);
    }
}
