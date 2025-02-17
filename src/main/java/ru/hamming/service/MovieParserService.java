package ru.hamming.service;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import ru.hamming.dto.MovieDto;
import ru.hamming.entity.Movie;
import ru.hamming.exception.MovieIsNotExistException;
import ru.hamming.exception.MovieNotFoundException;
import ru.hamming.tmdb.TmdbMovie;
import ru.hamming.tmdb.TmdbResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// отвечает за запрос к OMDB API и парсинг ответа
@Service
@Log4j
public class MovieParserService {

    private final RestTemplate restTemplate;
    private MovieService movieService;

    // ключ API из конфигурации приложения.
    // TODO наладить что бы брался из зависимостей а не статически записывать
    private final String OMDBapiKey = "43734117";
    private final String TMDBapiKey = "760308d77de61db1c05055541511305b";

    public MovieParserService(RestTemplate restTemplate,MovieService movieService) {
        this.restTemplate = restTemplate;
        this.movieService = movieService;
    }

    // Метод для получения фильма по названию из OMDB API
    public Movie getMovieFromApi(String title) {
        //---------------------------------------------------------------------------------------------------------
        // улучшенный код, который делает различные проверки
        // 1) проверка на доступность к API через метод isApiAvailable()
        // 2) безопасное получение самого фильма через метод fetchMovieFromApi()
        // 3) проверка что фильм был успешно получен тогда будем его конвертировать в нормалный формат через промежуточную безопастную сущность MovieDto

        String url = getAPIUrl(title);
        if (isApiAvailable(url)) {
            // безопасное получение фильма через посредника
            Optional<MovieDto> movieDto = fetchMovieFromApi(title);

            if (movieDto.isPresent()) {
                // добавлена логика обработки успехных запросов, но с пустыми данными
                Movie movie = convertToMovie(movieDto.get());
//                movieRepository.save(movie);
                movieService.saveMovie(movie);
                return movie;
            } else {
                log.warn("API не вернуло данных для фильма: {} " + title);
                throw new MovieNotFoundException("Фильм не найден: " + title);
            }
        } else {
            log.warn("API недоступно.");
            throw new MovieNotFoundException("OMDB API временно недоступно");
        }
    }

    // Метод для получения фильмов по году из TMDB API
    public List<Movie> getMoviesByYear(String year) {
        String url = getTMDbApiURL(year);
        ResponseEntity<TmdbResponse> response = restTemplate.getForEntity(url, TmdbResponse.class);
        // проверка на доступность API
        if (isTMDbApiAvailable(response)) {
            List<Movie> movies = response.getBody().getResults().stream()
                    .map(this::convertToMovie)
                    .collect(Collectors.toList());

            movieService.saveMoviesByYear(movies);
            return movies;
        }
        return Collections.emptyList();
    }
    // проверка на доступность к OMDB API
    private boolean isApiAvailable(String url) {
        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.HEAD, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            log.error("Ошибка на клиентской части (4xx): {} " + e.getStatusCode());
        } catch (HttpServerErrorException e) {
            log.error(e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error(e.getMessage());
        } catch (UnknownHttpStatusCodeException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    private boolean isTMDbApiAvailable(ResponseEntity<TmdbResponse> response) {
        return response.getStatusCode() == HttpStatus.OK && response.getBody() != null;
    }

    // безопасное получение самого фильма по названию
    private Optional<MovieDto> fetchMovieFromApi(String title){
        try {
            return Optional.ofNullable(restTemplate.getForObject(getAPIUrl(title), MovieDto.class));
        } catch (RestClientException e) {
            log.error(e);
            return Optional.empty();
        }

    }

    private String getAPIUrl(String title) {
        return "http://www.omdbapi.com/?t=" + title + "&apikey=" + OMDBapiKey;
    }

    public String getTMDbApiURL(String year) {
        return String.format("https://api.themoviedb.org/3/discover/movie?api_key=" + TMDBapiKey + "&primary_release_year=" + year);
    }

    private Movie convertToMovie(MovieDto movieDto)  throws MovieIsNotExistException {
        if (isExistMovieData(movieDto)) {
            log.error("API вернуло пустые данные");
            throw new MovieIsNotExistException("empty data");
        }
        return Movie.builder()
                .title(movieDto.getTitle())
                .director(movieDto.getDirector())
                .genre(movieDto.getGenre())
                .year(movieDto.getYear())
                .plot(movieDto.getPlot())
                .build();
    }

    private Movie convertToMovie(TmdbMovie tmdbMovie) {
        if (isExistMovieData(tmdbMovie)) {
            log.error("API вернуло пустые данные");
            throw new MovieIsNotExistException("empty data");
        }
        // TODO допилить дополнительная обработка для получения жанра и режиссера
        return Movie.builder()
                .title(tmdbMovie.getTitle())
                .year(tmdbMovie.getReleaseDate().substring(0, 4))
                .genre("временная заглушка")
                .director("временная заглушка")
                .plot(tmdbMovie.getOverview())
                .build();
    }

    // проверка на содержание данных при успешном запросе к OMDB API
    private boolean isExistMovieData(MovieDto movieDto) {
        return movieDto.getTitle() == null || movieDto.getTitle().trim().isEmpty()
                || movieDto.getDirector() == null || movieDto.getDirector().trim().isEmpty()
                || movieDto.getGenre() == null || movieDto.getGenre().trim().isEmpty()
                || movieDto.getYear() == null
                || movieDto.getPlot() == null || movieDto.getPlot().trim().isEmpty();
    }

    // проверка на содержание данных при успешном запросе к tmdb API
    private boolean isExistMovieData(TmdbMovie tmdbMovie) {
        // TODO добавить более сложную логику на проверку пустоты содержимого контента
        return tmdbMovie.getTitle() == null || tmdbMovie.getTitle().trim().isEmpty();
    }
}