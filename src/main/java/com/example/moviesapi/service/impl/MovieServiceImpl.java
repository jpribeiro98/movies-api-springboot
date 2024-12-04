package com.example.moviesapi.service.impl;

import com.example.moviesapi.exceptions.MovieNotFoundException;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.repository.MovieRepository;
import com.example.moviesapi.service.MovieService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie create(Movie movie) {
        return movieRepository.create(movie); 
    }

    @Override
    public List<Movie> createAll(List<Movie> movies) {
        return movieRepository.createAll(movies);
    }

    @Override
    public Movie findById(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> findByLaunchDate(LocalDate launchDate) {
        return movieRepository.findByLaunchDate(launchDate);
    }

    @Override
    public Movie update(Movie movie, Long id) {
        return movieRepository.update(movie, id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Override
    public void delete(Long id) {
        movieRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        movieRepository.deleteAll();
    }
}
