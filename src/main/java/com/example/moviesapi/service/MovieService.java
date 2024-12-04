package com.example.moviesapi.service;

import java.time.LocalDate;
import java.util.List;

import com.example.moviesapi.model.Movie;

public interface MovieService {
    Movie create(Movie movie);

    List<Movie> createAll(List<Movie> movies);

    Movie findById(Long id);

    List<Movie> findAll();

    List<Movie> findByLaunchDate(LocalDate launchDate);

    Movie update(Movie movie, Long id);

    void delete(Long id);

    void deleteAll();
}
