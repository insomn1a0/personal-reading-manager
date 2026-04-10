package com.example.library.controller;

import com.example.library.dto.BookDto;
import com.example.library.entity.ReadingStatus;
import com.example.library.service.AuthorService;
import com.example.library.service.BookService;
import com.example.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    @GetMapping
    public String listBooks(@RequestParam(required = false) String search,
                            @RequestParam(required = false) ReadingStatus status,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(defaultValue = "createdAt") String sort,
                            Model model) {
        model.addAttribute("books", bookService.findForList(search, status, categoryId, sort));
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("statuses", ReadingStatus.values());
        model.addAttribute("categories", categoryService.findAll());
        return "books/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        prepareForm(model, new BookDto(), "/books");
        return "books/form";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("book", bookService.findEntityById(id));
            return "books/details";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/books";
        }
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute("book") BookDto bookDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, bookDto, "/books");
            return "books/form";
        }

        try {
            bookService.create(bookDto);
            redirectAttributes.addFlashAttribute("message", "Book created successfully.");
            return "redirect:/books";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareForm(model, bookDto, "/books");
            return "books/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return bookService.findDtoById(id)
                .map(book -> {
                    prepareForm(model, book, "/books/" + id);
                    return "books/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Book not found.");
                    return "redirect:/books";
                });
    }

    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("book") BookDto bookDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, bookDto, "/books/" + id);
            return "books/form";
        }

        try {
            bookService.update(id, bookDto);
            redirectAttributes.addFlashAttribute("message", "Book updated successfully.");
            return "redirect:/books";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareForm(model, bookDto, "/books/" + id);
            return "books/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/books";
    }

    private void prepareForm(Model model, BookDto bookDto, String formAction) {
        model.addAttribute("book", bookDto);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("collections", bookService.findAvailableCollections());
        model.addAttribute("statuses", ReadingStatus.values());
        model.addAttribute("formAction", formAction);
    }
}
