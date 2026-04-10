package com.example.library.service;

import com.example.library.dto.CategoryViewDto;
import com.example.library.entity.Category;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<CategoryViewDto> findAllViews() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(category -> new CategoryViewDto(
                        category.getId(),
                        category.getName(),
                        bookRepository.countByCategories_Id(category.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
    }

    public Category create(String name) {
        Category category = new Category();
        category.setName(normalizeRequired(name));
        return categoryRepository.save(category);
    }

    public Category update(Long id, String name) {
        Category category = findById(id);
        category.setName(normalizeRequired(name));
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        if (bookRepository.existsByCategories_Id(id)) {
            throw new IllegalStateException("Cannot delete a category that is assigned to books.");
        }
        categoryRepository.deleteById(id);
    }

    public Category findOrCreateByName(String name) {
        String normalizedName = normalizeRequired(name);
        return categoryRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> categoryRepository.save(createCategory(normalizedName)));
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private String normalizeRequired(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        return normalized;
    }
}
