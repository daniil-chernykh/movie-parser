package ru.hamming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hamming.entity.Movie;
import ru.hamming.repository.MovieRepository;

// отвечает за работу с базой данных (сохранение и получение фильмов)
@Service
public class MovieService {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Сохраняем фильм
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie); // Сохраняем или обновляем фильм
    }

    // Ищем фильм по названию
    public Movie getMovieByTitle(String title) {
        return movieRepository.findByTitle(title); // Ищем фильм по названию
    }
}
