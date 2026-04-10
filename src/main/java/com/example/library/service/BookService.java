package com.example.library.service;

import com.example.library.dto.BookDto;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.ReadingStatus;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Book> findAllEntities() {
        return bookRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Book> findForList(String search, ReadingStatus status, Long categoryId, String sort) {
        return bookRepository.findAll().stream()
                .filter(book -> matchesSearch(book, search))
                .filter(book -> status == null || book.getStatus() == status)
                .filter(book -> categoryId == null || book.getCategories().stream().anyMatch(category -> category.getId().equals(categoryId)))
                .sorted(resolveComparator(sort))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Book> findRecentBooks(int limit) {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed()
                        .thenComparing(Comparator.comparing(Book::getId, Comparator.nullsLast(Long::compareTo)).reversed()))
                .limit(limit)
                .toList();
    }

    @Transactional(readOnly = true)
    public Set<String> findAvailableCollections() {
        TreeSet<String> collections = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        collections.addAll(List.of("Favorites", "Classics", "Study Books", "Sci-Fi Picks"));
        bookRepository.findAll().stream()
                .map(Book::getCollectionName)
                .filter(name -> name != null && !name.isBlank())
                .forEach(collections::add);
        return collections;
    }

    @Transactional(readOnly = true)
    public List<BookDto> findAllDtos() {
        return bookRepository.findAllByOrderByIdDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<BookDto> findDtoById(Long id) {
        return bookRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Book findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));
    }

    public BookDto create(BookDto bookDto) {
        Book savedBook = save(new Book(), bookDto);
        return toDto(savedBook);
    }

    public BookDto update(Long id, BookDto bookDto) {
        Book existingBook = findEntityById(id);
        Book savedBook = save(existingBook, bookDto);
        return toDto(savedBook);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found.");
        }
        bookRepository.deleteById(id);
    }

    private Book save(Book book, BookDto bookDto) {
        book.setTitle(normalizeRequired(bookDto.getTitle(), "Book title is required."));
        book.setIsbn(normalizeOptional(bookDto.getIsbn()));
        book.setPublishedYear(bookDto.getPublishedYear());
        book.setDescription(normalizeOptional(bookDto.getDescription()));
        book.setCoverImageUrl(normalizeOptional(bookDto.getCoverImageUrl()));
        book.setNotes(normalizeOptional(bookDto.getNotes()));
        book.setFavoriteQuote(normalizeOptional(bookDto.getFavoriteQuote()));
        book.setCollectionName(normalizeOptional(bookDto.getCollectionName()));
        book.setProgress(validateProgress(bookDto.getProgress()));
        book.setRating(validateRating(bookDto.getRating()));
        book.setStatus(bookDto.getStatus() == null ? ReadingStatus.WANT_TO_READ : bookDto.getStatus());
        updateCompletionDates(book);
        book.setAuthor(resolveAuthor(bookDto.getAuthorId()));
        book.setCategories(resolveCategories(bookDto.getCategoryIds()));
        return bookRepository.save(book);
    }

    private Author resolveAuthor(Long authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException("Please choose an author.");
        }
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Selected author does not exist."));
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new LinkedHashSet<>();
        if (categoryIds == null) {
            return categories;
        }

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Selected category does not exist."));
            categories.add(category);
        }
        return categories;
    }

    private BookDto toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setDescription(book.getDescription());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setNotes(book.getNotes());
        dto.setFavoriteQuote(book.getFavoriteQuote());
        dto.setCollectionName(book.getCollectionName());
        dto.setProgress(book.getProgress());
        dto.setRating(book.getRating());
        dto.setStatus(book.getStatus());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setCompletedAt(book.getCompletedAt());
        dto.setAuthorId(book.getAuthor() != null ? book.getAuthor().getId() : null);
        dto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return dto;
    }

    private boolean matchesSearch(Book book, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String normalizedSearch = search.trim().toLowerCase(Locale.ROOT);
        return normalizeOptional(book.getTitle()).toLowerCase(Locale.ROOT).contains(normalizedSearch)
                || normalizeOptional(book.getAuthor() != null ? book.getAuthor().getName() : "").toLowerCase(Locale.ROOT).contains(normalizedSearch)
                || normalizeOptional(book.getIsbn()).toLowerCase(Locale.ROOT).contains(normalizedSearch);
    }

    private Comparator<Book> resolveComparator(String sort) {
        String selectedSort = (sort == null || sort.isBlank()) ? "createdAt" : sort;
        return switch (selectedSort) {
            case "title" -> Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "year" -> Comparator.comparing(Book::getPublishedYear, Comparator.nullsLast(Integer::compareTo))
                    .reversed()
                    .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "rating" -> Comparator.comparing(Book::getRating, Comparator.nullsLast(Integer::compareTo))
                    .reversed()
                    .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Book::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo))
                    .reversed()
                    .thenComparing(Comparator.comparing(Book::getId, Comparator.nullsLast(Long::compareTo)).reversed());
        };
    }

    private Integer validateProgress(Integer progress) {
        if (progress == null) {
            return 0;
        }
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100.");
        }
        return progress;
    }

    private Integer validateRating(Integer rating) {
        if (rating == null) {
            return null;
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        return rating;
    }

    private void updateCompletionDates(Book book) {
        if (book.getStatus() == ReadingStatus.FINISHED) {
            if (book.getCompletedAt() == null) {
                book.setCompletedAt(LocalDateTime.now());
            }
            if (book.getProgress() != null && book.getProgress() < 100) {
                book.setProgress(100);
            }
            return;
        }
        book.setCompletedAt(null);
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeOptional(value);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }
}
