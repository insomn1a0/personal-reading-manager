package com.example.library.service;

import com.example.library.dto.AuthorViewDto;
import com.example.library.entity.Author;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<AuthorViewDto> findAllViews() {
        return authorRepository.findAllByOrderByNameAsc().stream()
                .map(author -> new AuthorViewDto(
                        author.getId(),
                        author.getName(),
                        author.getBio(),
                        bookRepository.countByAuthorId(author.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found."));
    }

    public Author create(String name, String bio) {
        Author author = new Author();
        author.setName(normalizeRequired(name, "Author name is required."));
        author.setBio(normalizeOptional(bio));
        return authorRepository.save(author);
    }

    public Author update(Long id, String name, String bio) {
        Author author = findById(id);
        author.setName(normalizeRequired(name, "Author name is required."));
        author.setBio(normalizeOptional(bio));
        return authorRepository.save(author);
    }

    public void delete(Long id) {
        if (bookRepository.existsByAuthorId(id)) {
            throw new IllegalStateException("Cannot delete an author that still has books.");
        }
        authorRepository.deleteById(id);
    }

    public Author findOrCreateByName(String name) {
        String normalizedName = normalizeRequired(name, "Author name is required.");
        return authorRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> authorRepository.save(createAuthor(normalizedName)));
    }

    private Author createAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        author.setBio("");
        return author;
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
