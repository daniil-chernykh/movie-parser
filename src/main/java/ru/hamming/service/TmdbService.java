package ru.hamming.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TmdbService {
    private final String tmdbApiKey = "760308d77de61db1c05055541511305b";
    private final RestTemplate restTemplate;

    public TmdbService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


}
