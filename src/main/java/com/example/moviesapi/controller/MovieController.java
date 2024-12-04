package com.example.moviesapi.controller;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.service.MovieService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Movie create(@Valid @RequestBody Movie movie) {
        return movieService.create(movie);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/list")
    public List<Movie> createAll(@RequestBody List<@Valid Movie> movies) {
        return movieService.createAll(movies);
    }

    @GetMapping(value = "/{id}")
    public Movie findById(@PathVariable Long id) {
        return movieService.findById(id);
    }

    @GetMapping("")
    public List<Movie> findAll() {
        return movieService.findAll();
    }

    @GetMapping(value = "/launchDate/{launchDate}")
    public List<Movie> findByLaunchDate(@Valid @PastOrPresent @PathVariable LocalDate launchDate) {
        return movieService.findByLaunchDate(launchDate);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}")
    public Movie update(@Valid @RequestBody Movie movie, @PathVariable Long id) {
        return movieService.update(movie, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        movieService.delete(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    public void deleteAll() {
        movieService.deleteAll();
    }
}
