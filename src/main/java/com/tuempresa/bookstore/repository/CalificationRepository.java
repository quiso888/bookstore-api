package com.tuempresa.bookstore.repository;

import com.tuempresa.bookstore.model.Calification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalificationRepository extends JpaRepository<Calification, Long> {

    List<Calification> findByBookId(Long bookId);

    List<Calification> findByUserId(Long userId);

    Optional<Calification> findByUserIdAndBookId(Long userId, Long bookId);
}
