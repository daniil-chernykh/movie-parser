package ru.hamming.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

// сущность для маппинга данных TMDb
@Getter
@Setter
public class TmdbMovie {
    private String title;
    @JsonProperty("release_date")
    private String releaseDate;
    private String overview;
}
