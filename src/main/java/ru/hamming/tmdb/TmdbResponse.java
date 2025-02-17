package ru.hamming.tmdb;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbResponse {
    private List<TmdbMovie> results;
}
