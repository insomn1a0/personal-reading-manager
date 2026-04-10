package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.OpenLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/import/isbn")
@RequiredArgsConstructor
public class IsbnImportController {

    private final OpenLibraryService openLibraryService;

    @GetMapping
    public String importPage() {
        return "import/index";
    }

    @PostMapping
    public String importByIsbn(@RequestParam String isbn, RedirectAttributes redirectAttributes) {
        try {
            Book book = openLibraryService.importByIsbn(isbn);
            redirectAttributes.addFlashAttribute("message", "Book imported successfully.");
            return "redirect:/books/" + book.getId() + "/edit";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/import/isbn";
        }
    }
}
