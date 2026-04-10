package com.example.library.controller;

import com.example.library.service.BookService;
import com.example.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StatisticsService statisticsService;
    private final BookService bookService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stats", statisticsService.getDashboardStats());
        model.addAttribute("recentBooks", bookService.findRecentBooks(5));
        return "dashboard/index";
    }
}
