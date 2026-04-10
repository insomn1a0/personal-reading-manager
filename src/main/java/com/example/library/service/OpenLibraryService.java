package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.ReadingStatus;
import com.example.library.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class OpenLibraryService {

    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final RestTemplateBuilder restTemplateBuilder;

    public Book importByIsbn(String isbn) {
        String normalizedIsbn = normalizeIsbn(isbn);
        if (normalizedIsbn.isBlank()) {
            throw new IllegalArgumentException("ISBN is required.");
        }

        Book existingBook = bookRepository.findByIsbnIgnoreCase(normalizedIsbn).orElse(null);
        if (existingBook != null) {
            return existingBook;
        }

        JsonNode bookNode = fetchBookNode(normalizedIsbn);
        String title = requiredText(bookNode, "title", "Book title not found in Open Library.");
        String authorName = extractAuthorName(bookNode);
        Integer publishedYear = extractPublishedYear(bookNode.path("publish_date").asText(""));
        Set<Category> categories = extractCategories(bookNode);

        Author author = authorService.findOrCreateByName(authorName);

        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(normalizedIsbn);
        book.setPublishedYear(publishedYear);
        book.setDescription("Imported from Open Library by ISBN.");
        book.setCoverImageUrl(extractCoverImageUrl(bookNode));
        book.setNotes("");
        book.setFavoriteQuote("");
        book.setCollectionName("");
        book.setProgress(0);
        book.setRating(null);
        book.setStatus(ReadingStatus.WANT_TO_READ);
        book.setAuthor(author);
        book.setCategories(categories);

        return bookRepository.save(book);
    }

    private JsonNode fetchBookNode(String isbn) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response == null || response.isEmpty()) {
            throw new IllegalArgumentException("No Open Library data found for ISBN " + isbn + ".");
        }

        JsonNode bookNode = response.path("ISBN:" + isbn);
        if (bookNode.isMissingNode() || bookNode.isEmpty()) {
            throw new IllegalArgumentException("No Open Library data found for ISBN " + isbn + ".");
        }
        return bookNode;
    }

    private String extractAuthorName(JsonNode bookNode) {
        JsonNode authors = bookNode.path("authors");
        if (authors.isArray() && !authors.isEmpty()) {
            String name = authors.get(0).path("name").asText("").trim();
            if (!name.isBlank()) {
                return name;
            }
        }
        return "Unknown Author";
    }

    private Set<Category> extractCategories(JsonNode bookNode) {
        Set<Category> categories = new LinkedHashSet<>();
        JsonNode subjects = bookNode.path("subjects");
        if (!subjects.isArray()) {
            return categories;
        }

        for (JsonNode subject : subjects) {
            String subjectName = subject.path("name").asText("").trim();
            if (!subjectName.isBlank()) {
                categories.add(categoryService.findOrCreateByName(subjectName));
            }
            if (categories.size() >= 3) {
                break;
            }
        }
        return categories;
    }

    private Integer extractPublishedYear(String publishDate) {
        Matcher matcher = YEAR_PATTERN.matcher(publishDate);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private String extractCoverImageUrl(JsonNode bookNode) {
        return bookNode.path("cover").path("medium").asText("").trim();
    }

    private String normalizeIsbn(String isbn) {
        return isbn == null ? "" : isbn.replaceAll("[^0-9Xx]", "").trim();
    }

    private String requiredText(JsonNode node, String fieldName, String message) {
        String value = node.path(fieldName).asText("").trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
