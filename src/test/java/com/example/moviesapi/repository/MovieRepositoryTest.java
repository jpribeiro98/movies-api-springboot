package com.example.moviesapi.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.moviesapi.model.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
class MovieRepositoryTest {
	
	private MovieRepository movieRepository;
	
	@Autowired
	public MovieRepositoryTest(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
		this.movieRepository = new MovieRepository(jdbcClient, jdbcTemplate);
	}
	
	@BeforeAll
	public static void setUp(@Autowired JdbcTemplate jdbcTemplate) {
		jdbcTemplate.update("INSERT INTO Movie (title, launchDate, rating, revenue)"
				+ "VALUES ('Pulp Fiction', '1994-10-14', 8.9, 212891598),"
				+ "('Goodfellas', '1990-11-23', 8.7, 47103483),"
				+ "('The Godfather', '1972-10-24', 9.2, 270007394)");
		
		
	}
	
	private void assertMovieEquals(Movie expected, Movie actual) {
	    assertEquals(expected.getId(), actual.getId());
	    assertEquals(expected.getTitle(), actual.getTitle());
	    assertEquals(expected.getLaunchDate(), actual.getLaunchDate());
	    assertEquals(0, expected.getRating().compareTo(actual.getRating()));
	    assertEquals(expected.getRevenue(), actual.getRevenue());
	}
	
	
	@Test
	void shouldCreateAndReturnMovie() {
		Movie movie = new Movie("Forrest Gump", LocalDate.of(1994, 10, 28), new BigDecimal(8.0), Long.valueOf(679835137));
		Movie returnedMovie = movieRepository.create(movie);
		List<Movie> movies = movieRepository.findAll();
		Movie createdMovie = movies.get(3);
		
		assertNotNull(returnedMovie);
		assertNotNull(createdMovie);
		assertEquals(4, movies.size());
		
		assertMovieEquals(returnedMovie, createdMovie);
		
		assertEquals("Forrest Gump", createdMovie.getTitle());
		assertEquals(LocalDate.of(1994, 10, 28), createdMovie.getLaunchDate());
		assertEquals(BigDecimal.valueOf(8.0), createdMovie.getRating());
		assertEquals(Long.valueOf(679835137), createdMovie.getRevenue());
		
	}
	
	@Test
	void shouldCreateAndReturnAllMovies() {
		List<Movie> returnedMovies = movieRepository.createAll(List.of(new Movie("Forrest Gump", LocalDate.of(1994, 10, 28), new BigDecimal(8.0), Long.valueOf(679835137))
				,new Movie("Schindler's List", LocalDate.of(1994, 3, 4), new BigDecimal(9.0), Long.valueOf(322197130))
				,new Movie("The Dark Knight", LocalDate.of(2008, 7, 24), new BigDecimal(9.0), Long.valueOf(1007695772))));
		
		List<Movie> movies = movieRepository.findAll();
		
		assertNotNull(returnedMovies);
		assertEquals(6, movies.size());
		
        assertMovieEquals(returnedMovies.get(0), movies.get(3));
		assertMovieEquals(returnedMovies.get(1), movies.get(4));
		assertMovieEquals(returnedMovies.get(2), movies.get(5));
		
		assertEquals("Forrest Gump", movies.get(3).getTitle());
		assertEquals("Schindler's List", movies.get(4).getTitle());
		assertEquals("The Dark Knight", movies.get(5).getTitle());
	}
	
	@Test
	void shouldFindMovieWithValidId_ReturnMovie() {
		Optional<Movie> movie = movieRepository.findById(1L);
		
		assertTrue(movie.isPresent());

	    Movie foundMovie = movie.get(); 

	    assertEquals(1L, foundMovie.getId());
	    assertEquals("Pulp Fiction", foundMovie.getTitle());
	    assertEquals(LocalDate.of(1994, 10, 14), foundMovie.getLaunchDate());
	    assertEquals(BigDecimal.valueOf(8.9), foundMovie.getRating());
	    assertEquals(Long.parseLong("212891598"), foundMovie.getRevenue());
	}
	
	@Test
	void shouldNotFindMovieWithInvalidId_ReturnEmptyOptional() {
		Optional<Movie> movie = movieRepository.findById(10L);
		
		assertFalse(movie.isPresent());
	}
	
	@Test
	void shouldFindAllMovies() {
		List<Movie> movies = movieRepository.findAll();
		
		assertNotNull(movies);
		assertEquals(3, movies.size());
		
		assertEquals(1L,movies.get(0).getId());
		assertEquals("Pulp Fiction",movies.get(0).getTitle());
		
		assertEquals(2L,movies.get(1).getId());
		assertEquals("Goodfellas",movies.get(1).getTitle());
		
		assertEquals(3L,movies.get(2).getId());
		assertEquals("The Godfather",movies.get(2).getTitle());
	}
	
	@Test 
	void shouldFindMoviesByLaunchDate() {
		Movie movie = new Movie("O Padrinho", LocalDate.of(1972, 10, 24), new BigDecimal("9.2"), Long.parseLong("270007394"));
		movieRepository.create(movie);
		List<Movie> sameLaunchDateMovies = movieRepository.findByLaunchDate(LocalDate.of(1972, 10, 24));
		List<Movie> movies = movieRepository.findAll();
		
		assertNotNull(sameLaunchDateMovies);
		assertEquals(2, sameLaunchDateMovies.size());
		
		assertMovieEquals(movies.get(2), sameLaunchDateMovies.get(0));
		assertMovieEquals(movies.get(3), sameLaunchDateMovies.get(1));
		
		assertEquals("The Godfather",sameLaunchDateMovies.get(0).getTitle());
		assertEquals("O Padrinho",sameLaunchDateMovies.get(1).getTitle());
	}
	
	@Test
	void shouldUpdateMovieWithValidId_ReturnMovie() {
		Movie movie = movieRepository.findById(2L).get();
		movie.setTitle("Tudo Bons Rapazes");
		Optional<Movie> optReturnedMovie = movieRepository.update(movie, 2L);
		Movie createdMovie = movieRepository.findById(2L).get();
		
		assertTrue(optReturnedMovie.isPresent());
		Movie returnedMovie = optReturnedMovie.get();
		
		assertNotNull(createdMovie);
		assertMovieEquals(returnedMovie,createdMovie);
		assertEquals("Tudo Bons Rapazes", createdMovie.getTitle());
	}
	
	@Test
	void shouldNotUpdateMovieWithInvalidId_ReturnEmptyOptional() {
		Movie movie = movieRepository.findById(2L).get();
		movie.setTitle("Tudo Bons Rapazes");
		Optional<Movie> optReturnedMovie = movieRepository.update(movie, 10L);
		
		assertFalse(optReturnedMovie.isPresent());
	}
	
	@Test
	void shouldDeleteMovieWithValidId() {
		movieRepository.delete(1L);
		assertEquals(2, movieRepository.findAll().size());
	}
	
	@Test
	void shouldNotDeleteMovieWithInvalidId() {
		movieRepository.delete(10L);
		assertEquals(3, movieRepository.findAll().size());
	}
	
	@Test
	void shouldDeleteAllMovies() {
		movieRepository.deleteAll();
		assertEquals(0, movieRepository.findAll().size());
	}
}


