package ru.hamming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hamming.entity.Movie;
import ru.hamming.service.MovieParserService;
import ru.hamming.service.MovieService;

import java.util.List;


@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieParserService movieParserService;
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieParserService movieParserService, MovieService movieService) {
        this.movieParserService = movieParserService;
        this.movieService = movieService;
    }
    @GetMapping("/movie")
    public Movie getMovie(@RequestParam String title) {
        return movieParserService.getMovieFromApi(title);
    }

    @GetMapping("/import")
    public List<Movie> getMoviesByYear(@RequestParam(name = "year", required = true) String year) {
        return movieParserService.getMoviesByYear(year);
    }
}
