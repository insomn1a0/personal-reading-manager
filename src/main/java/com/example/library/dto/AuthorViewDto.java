package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorViewDto {

    private Long id;
    private String name;
    private String bio;
    private long bookCount;
}
