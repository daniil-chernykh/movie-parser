package ru.hamming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hamming.entity.Movie;
import ru.hamming.service.MovieParserService;
import ru.hamming.service.MovieService;


@RestController
public class MovieController {

    @Autowired
    private MovieParserService movieParserService;
    @Autowired
    private final MovieService movieService;

    public MovieController(MovieParserService movieParserService, MovieService movieService) {
        this.movieParserService = movieParserService;
        this.movieService = movieService;
    }

    @GetMapping("/movie")
    public Movie getMovie(@RequestParam String title) {
        return movieParserService.getMovieFromApi(title);
    }

//    @GetMapping("/movies/year")
//    public List<Movie> getMoviesByYear(@RequestParam String year) {
//        return movieService.getOrFetchMoviesByYear(year, movieParserService);
//    }

}
