package ru.hamming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hamming.entity.Movie;
import ru.hamming.service.MovieParserService;
import ru.hamming.service.MovieService;

@RestController
public class MovieController {

    @Autowired
    private MovieParserService movieParserService;

    @GetMapping("/movie")
    public Movie getMovie(@RequestParam String title) {
        return movieParserService.getMovieFromApi(title);
    }
}
