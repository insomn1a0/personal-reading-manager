package com.example.library.controller;

import com.example.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorPageController {

    private final AuthorService authorService;

    @GetMapping
    public String authorsPage(Model model) {
        model.addAttribute("authors", authorService.findAllViews());
        return "authors/index";
    }

    @PostMapping
    public String createAuthor(@RequestParam String name,
                               @RequestParam(required = false) String bio,
                               RedirectAttributes redirectAttributes) {
        try {
            authorService.create(name, bio);
            redirectAttributes.addFlashAttribute("message", "Author created successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/authors";
    }

    @PostMapping("/{id}")
    public String updateAuthor(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String bio,
                               RedirectAttributes redirectAttributes) {
        try {
            authorService.update(id, name, bio);
            redirectAttributes.addFlashAttribute("message", "Author updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/authors";
    }

    @PostMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            authorService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Author deleted successfully.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/authors";
    }
}
