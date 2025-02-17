package ru.hamming.service;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import ru.hamming.dto.MovieDto;
import ru.hamming.entity.Movie;
import ru.hamming.exception.MovieIsNotExistException;
import ru.hamming.exception.MovieNotFoundException;
import java.util.Optional;

// отвечает за запрос к OMDB API и парсинг ответа
@Service
@Log4j
public class MovieParserService {

    private final RestTemplate restTemplate;
    private MovieService movieService;

    // ключ API из конфигурации приложения.
    // TODO наладить что бы брался из зависимостей а не статически записывать
    private final String apiKey = "43734117";

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
                Movie movie = convertDtoToMovie(movieDto.get());
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

//    // Запрос списка фильмов по году из API
//    public List<Movie> fetchMoviesFromApiByYear(String year) {
//        String url = "http://www.omdbapi.com/?y=" + year + "&apikey=" + apiKey;
//        log.info("Запрос к API: " + url);
//
//        try {
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            log.info("Ответ API: " + response.getBody());
//
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                MovieSearchResultByCategory result = new ObjectMapper().readValue(response.getBody(), MovieSearchResultByCategory.class);
//                log.info("Найдено фильмов: " + result.getSearch().size());
//
//                return result.getSearch().stream()
//                        .map(this::convertDtoToMovie)
//                        .toList();
//            } else {
//                log.warn("API вернуло пустой список фильмов за " + year);
//            }
//        } catch (Exception e) {
//            log.error("Ошибка при запросе к API: ", e);
//        }
//
//        return List.of();
//    }

    // проверка на доступность к API
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
        return "http://www.omdbapi.com/?t=" + title + "&apikey=" + apiKey;
    }

    private Movie convertDtoToMovie(MovieDto movieDto)  throws MovieIsNotExistException {
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

    private boolean isExistMovieData(MovieDto movieDto) {
        return movieDto.getTitle() == null || movieDto.getTitle().trim().isEmpty()
                || movieDto.getDirector() == null || movieDto.getDirector().trim().isEmpty()
                || movieDto.getGenre() == null || movieDto.getGenre().trim().isEmpty()
                || movieDto.getYear() == null
                || movieDto.getPlot() == null || movieDto.getPlot().trim().isEmpty();
    }
}