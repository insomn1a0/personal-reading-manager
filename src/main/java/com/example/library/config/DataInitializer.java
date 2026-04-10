package com.example.library.config;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.ReadingStatus;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(AuthorRepository authorRepository,
                               CategoryRepository categoryRepository,
                               BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            Author murakami = authorRepository.save(createAuthor("Haruki Murakami", "Japanese novelist known for surreal and introspective fiction."));
            Author rowling = authorRepository.save(createAuthor("J. K. Rowling", "British author of the Harry Potter series."));
            Author clear = authorRepository.save(createAuthor("James Clear", "Author focused on habits, productivity, and personal development."));
            Author herbert = authorRepository.save(createAuthor("Frank Herbert", "American science fiction author best known for Dune."));

            Category fiction = categoryRepository.save(createCategory("Fiction"));
            Category fantasy = categoryRepository.save(createCategory("Fantasy"));
            Category productivity = categoryRepository.save(createCategory("Productivity"));
            Category favorites = categoryRepository.save(createCategory("Favorites"));
            Category philosophy = categoryRepository.save(createCategory("Philosophy"));
            Category classics = categoryRepository.save(createCategory("Classics"));
            Category sciFi = categoryRepository.save(createCategory("Sci-Fi"));

            bookRepository.saveAll(List.of(
                    createBook(
                            "Kafka on the Shore",
                            "9781400079278",
                            2005,
                            "A surreal coming-of-age novel that blends dream logic, memory, and myth.",
                            "https://covers.openlibrary.org/b/isbn/9781400079278-M.jpg",
                            "Re-read chapter 15 before the lab presentation. Strong example for discussing literary themes.",
                            "\"Memories warm you up from the inside. But they also tear you apart.\"",
                            "Favorites",
                            100,
                            5,
                            ReadingStatus.FINISHED,
                            murakami,
                            linkedSet(fiction, favorites, philosophy),
                            LocalDateTime.now().minusDays(18),
                            LocalDateTime.now().minusDays(2)
                    ),
                    createBook(
                            "Harry Potter and the Philosopher's Stone",
                            "9780747532699",
                            1997,
                            "A modern fantasy classic and an easy example of category tagging and cover display.",
                            "https://covers.openlibrary.org/b/isbn/9780747532699-M.jpg",
                            "Useful sample data because everyone recognizes it quickly.",
                            "\"It does not do to dwell on dreams and forget to live.\"",
                            "Classics",
                            40,
                            4,
                            ReadingStatus.READING,
                            rowling,
                            linkedSet(fantasy, favorites, classics),
                            LocalDateTime.now().minusDays(8),
                            null
                    ),
                    createBook(
                            "Atomic Habits",
                            "9781847941831",
                            2018,
                            "A practical book about building good habits and improving consistency.",
                            "https://covers.openlibrary.org/b/isbn/9781847941831-M.jpg",
                            "Good dashboard sample because it is still planned and not started.",
                            "\"You do not rise to the level of your goals. You fall to the level of your systems.\"",
                            "Study Books",
                            0,
                            null,
                            ReadingStatus.WANT_TO_READ,
                            clear,
                            linkedSet(productivity),
                            LocalDateTime.now().minusDays(3),
                            null
                    ),
                    createBook(
                            "Dune",
                            "9780441172719",
                            1965,
                            "Epic science fiction with politics, prophecy, and world-building that looks great in a catalog layout.",
                            "https://covers.openlibrary.org/b/isbn/9780441172719-M.jpg",
                            "Paused after the first third, but the world-building is still useful as demo data.",
                            "\"Fear is the mind-killer.\"",
                            "Sci-Fi Picks",
                            22,
                            3,
                            ReadingStatus.DNF,
                            herbert,
                            linkedSet(sciFi, fiction),
                            LocalDateTime.now().minusDays(12),
                            null
                    )
            ));
        };
    }

    private Author createAuthor(String name, String bio) {
        Author author = new Author();
        author.setName(name);
        author.setBio(bio);
        return author;
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private Book createBook(String title,
                            String isbn,
                            Integer year,
                            String description,
                            String coverImageUrl,
                            String notes,
                            String favoriteQuote,
                            String collectionName,
                            Integer progress,
                            Integer rating,
                            ReadingStatus status,
                            Author author,
                            LinkedHashSet<Category> categories,
                            LocalDateTime createdAt,
                            LocalDateTime completedAt) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublishedYear(year);
        book.setDescription(description);
        book.setCoverImageUrl(coverImageUrl);
        book.setNotes(notes);
        book.setFavoriteQuote(favoriteQuote);
        book.setCollectionName(collectionName);
        book.setProgress(progress);
        book.setRating(rating);
        book.setStatus(status);
        book.setAuthor(author);
        book.setCategories(categories);
        book.setCreatedAt(createdAt);
        book.setCompletedAt(completedAt);
        return book;
    }

    private final LinkedHashSet<Category> linkedSet(Category... categories) {
        return new LinkedHashSet<>(List.of(categories));
    }
}
