package com.example.library.dto;

import com.example.library.entity.ReadingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Title is required.")
    private String title;

    private String isbn;

    @Min(value = 0, message = "Published year cannot be negative.")
    private Integer publishedYear;

    @Size(max = 2000, message = "Description is too long.")
    private String description;

    @NotNull(message = "Please choose an author.")
    private Long authorId;

    private Set<Long> categoryIds = new LinkedHashSet<>();

    @Size(max = 1000, message = "Cover image URL is too long.")
    private String coverImageUrl;

    @Size(max = 3000, message = "Notes are too long.")
    private String notes;

    @Size(max = 1000, message = "Favorite quote is too long.")
    private String favoriteQuote;

    @Size(max = 255, message = "Collection name is too long.")
    private String collectionName;

    @NotNull(message = "Progress is required.")
    @Min(value = 0, message = "Progress must be between 0 and 100.")
    @Max(value = 100, message = "Progress must be between 0 and 100.")
    private Integer progress = 0;

    @Min(value = 1, message = "Rating must be between 1 and 5.")
    @Max(value = 5, message = "Rating must be between 1 and 5.")
    private Integer rating;

    @NotNull(message = "Please choose a reading status.")
    private ReadingStatus status = ReadingStatus.WANT_TO_READ;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
