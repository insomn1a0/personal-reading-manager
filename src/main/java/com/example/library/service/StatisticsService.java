package com.example.library.service;

import com.example.library.dto.DashboardStatsDto;
import com.example.library.entity.Book;
import com.example.library.entity.ReadingStatus;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Value("${app.reading-goal:12}")
    private int readingGoal;

    public DashboardStatsDto getDashboardStats() {
        List<Book> books = bookRepository.findAll();
        double averageRating = books.stream()
                .filter(book -> book.getRating() != null)
                .mapToInt(Book::getRating)
                .average()
                .orElse(0.0);
        int averageProgress = (int) Math.round(books.stream()
                .filter(book -> book.getProgress() != null)
                .mapToInt(Book::getProgress)
                .average()
                .orElse(0.0));
        long finishedBooks = bookRepository.countByStatus(ReadingStatus.FINISHED);
        int goalProgressPercent = readingGoal <= 0
                ? 0
                : (int) Math.min(100, Math.round((finishedBooks * 100.0) / readingGoal));

        return new DashboardStatsDto(
                bookRepository.count(),
                authorRepository.count(),
                categoryRepository.count(),
                bookRepository.countByStatus(ReadingStatus.WANT_TO_READ),
                bookRepository.countByStatus(ReadingStatus.READING),
                finishedBooks,
                bookRepository.countByStatus(ReadingStatus.DNF),
                averageRating,
                averageProgress,
                readingGoal,
                finishedBooks,
                goalProgressPercent
        );
    }
}
