package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalBooks;
    private long totalAuthors;
    private long totalCategories;
    private long wantToReadBooks;
    private long readingBooks;
    private long finishedBooks;
    private long dnfBooks;
    private double averageRating;
    private int averageProgress;
    private int readingGoal;
    private long goalCompletedBooks;
    private int goalProgressPercent;
}
