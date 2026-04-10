package com.example.library.repository;

import com.example.library.entity.Book;
import com.example.library.entity.ReadingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(attributePaths = {"author", "categories"})
    List<Book> findAll();

    @EntityGraph(attributePaths = {"author", "categories"})
    List<Book> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"author", "categories"})
    List<Book> findTop5ByOrderByCreatedAtDescIdDesc();

    @Override
    @EntityGraph(attributePaths = {"author", "categories"})
    Optional<Book> findById(Long id);

    Optional<Book> findByIsbnIgnoreCase(String isbn);

    boolean existsByAuthorId(Long authorId);

    boolean existsByCategories_Id(Long categoryId);

    long countByStatus(ReadingStatus status);

    long countByAuthorId(Long authorId);

    long countByCategories_Id(Long categoryId);
}
