package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryViewDto {

    private Long id;
    private String name;
    private long bookCount;
}
