package ru.hamming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hamming.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie findByTitle(String title);
}
