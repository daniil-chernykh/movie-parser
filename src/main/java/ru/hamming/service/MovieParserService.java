package ru.hamming.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hamming.entity.Movie;
import ru.hamming.repository.MovieRepository;

import java.util.HashMap;
import java.util.Map;

// отвечает за запрос к OMDB API и парсинг ответа
@Service
public class MovieParserService {

    private final RestTemplate restTemplate;
    private MovieRepository movieRepository;

    // ключ API из конфигурации приложения
    // TODO наладить что бы брался из зависимостей а не статически записывать
    private final String apiKey = "43734117";

    public MovieParserService(RestTemplate restTemplate, MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    // Метод для получения фильма по названию из OMDB API
    public Movie getMovieFromApi(String title) {
        String url = "http://www.omdbapi.com/?t=" + title + "&apikey=" + apiKey;
        Movie movie = restTemplate.getForObject(url, Movie.class);

        if (movie != null) {
            movieRepository.save(movie);
        }

        return movie;
    }
}