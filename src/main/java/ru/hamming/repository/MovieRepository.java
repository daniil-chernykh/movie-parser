package ru.hamming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hamming.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie findByTitle(String title);

    // поиск фильмов по жанру
    List<Movie> findByGenre(String genre);

    List<Movie> findByYear(String year);

    List<Movie> findByDirector(String director);

    List<Movie> findByTitleAndDirector(String title, String director);

//    List<Movie> saveListMovies(List<Movie> movies);

}
