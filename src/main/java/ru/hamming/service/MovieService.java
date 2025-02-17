package ru.hamming.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hamming.entity.Movie;
import ru.hamming.repository.MovieRepository;

import java.util.List;

// отвечает за работу с базой данных (сохранение и получение фильмов)
@Service
@Log4j
public class MovieService {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Сохраняем фильм
    public Movie saveMovie(Movie movie) {
        List<Movie> existingMovies = movieRepository.findByTitleAndDirector(movie.getTitle(), movie.getDirector());

        // если уже есть подобный фильм
        if (!existingMovies.isEmpty()) {
            Movie existingMovie = existingMovies.get(0);

            existingMovie.setTitle(movie.getTitle());
            existingMovie.setGenre(movie.getGenre());
            existingMovie.setDirector(movie.getDirector());
            existingMovie.setPlot(movie.getPlot());
            existingMovie.setYear(movie.getYear());

            log.info("данные о фильме:" + movie.toString() + " обновлены");
            return movieRepository.save(existingMovie);
        }

        log.info("Фильм успешно сохранен");
        return movieRepository.save(movie); // Сохраняем или обновляем фильм
    }

    // ищем фильм по названию
    public Movie getMovieByTitle(String title) {
        return movieRepository.findByTitle(title); // Ищем фильм по названию
    }


}
