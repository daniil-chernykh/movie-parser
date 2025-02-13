package ru.hamming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.hamming.entity.Movie;
import ru.hamming.service.MovieParserService;

@SpringBootApplication
public class MovieParserApplication {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    public static void main(String[] args) {

        SpringApplication.run(MovieParserApplication.class,args);
//        MovieParserService service = new MovieParserService(new RestTemplate());
//        Movie movie = service.getMovieFromApi("anora");
//        System.out.println(movie.getTitle() + " - " + movie.getYear());
    }
}
