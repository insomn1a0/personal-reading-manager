# Personal Reading Manager

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Server_Rendered-005F0F?style=for-the-badge)
![H2](https://img.shields.io/badge/Database-H2-1E88E5?style=for-the-badge)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

A modern Spring Boot web application for tracking books, reading progress, notes, and library organization.  
It is designed to look polished enough for a portfolio while staying simple enough for a university lab defense.

## Overview

This project combines a clean Thymeleaf UI, practical CRUD flows, and lightweight reading-tracker features inspired by Goodreads, StoryGraph, and Open Library.  
It focuses on readable code, fast setup, and a presentable user experience rather than enterprise complexity.

## Features

- `📚` CRUD for books, authors, and categories
- `🔎` Search books by title, author, or ISBN
- `🧭` Filter books by reading status and category
- `↕️` Sort books by title, year, rating, and created date
- `🌐` ISBN import from the Open Library API
- `📊` Dashboard with statistics, status breakdown, rating average, and yearly goal progress
- `🌙` Light / dark theme switch with `localStorage` persistence
- `📝` Reading progress, notes, favorite quote, and cover image support
- `🏷️` Category tags and lightweight shelves / collections
- `🎯` Seed data for a strong first demo experience

## Tech Stack

- Java 17
- Spring Boot 3
- Thymeleaf
- Spring Data JPA
- H2 Database
- Maven
- Bootstrap 5


## How To Run

### Run with Maven

```bash
mvn spring-boot:run
```

### Build and run the JAR

```bash
mvn clean package
java -jar target/library-0.0.1-SNAPSHOT.jar
```

### H2 Console

After starting the app, open:

- Application: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

Default H2 settings:

- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa`
- Password: empty

## Seed Data

The application includes sample data on first launch so the UI is immediately useful for demos and screenshots.

Included sample content:

- authors
- books
- categories
- category-style tags / groupings
- shelves / collections such as `Favorites`, `Classics`, `Study Books`, and `Sci-Fi Picks`








