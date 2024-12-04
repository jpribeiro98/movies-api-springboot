package com.example.moviesapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.moviesapi.exceptions.MovieNotFoundException;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.service.impl.MovieServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MovieServiceImpl mockMovieService;

    private final List<Movie> movies = new ArrayList<>();


    @BeforeEach
    void setUp() {
        movies.add(new Movie(1L, "Pulp Fiction", LocalDate.of(1994, 10, 14), new BigDecimal("8.9"), Long.parseLong("212891598")));
        movies.add(new Movie(2L, "Goodfellas", LocalDate.of(1990, 11, 23), new BigDecimal("8.7"), Long.parseLong("47103483")));
        movies.add(new Movie(3L, "The Godfather", LocalDate.of(1972, 10, 24), new BigDecimal("9.2"), Long.parseLong("270007394")));
        movies.add(new Movie(4L, "O Padrinho", LocalDate.of(1972, 10, 24), new BigDecimal("9.2"), Long.parseLong("270007394")));
        
    }


    @Test
    void shouldCreateValidMovie() throws Exception {
        Movie movie = new Movie("Forrest Gump", LocalDate.of(1994, 10, 28), new BigDecimal(8.0), Long.valueOf(679835137));
        when(mockMovieService.create(any(Movie.class))).thenReturn(new Movie(5L, "Forrest Gump", LocalDate.of(1994, 10, 28), new BigDecimal(8.0), Long.valueOf(679835137)));
        
        mvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("application/json", result.getResponse().getContentType()))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andExpect(jsonPath("$.launchDate").value(movie.getLaunchDate().toString()))
                .andExpect(jsonPath("$.rating").value(movie.getRating().doubleValue()))
                .andExpect(jsonPath("$.revenue").value(movie.getRevenue().longValue()));

        
        verify(mockMovieService).create(any(Movie.class));
    }
    
    @Test
    void shouldNotCreateMovie_withNullAndEmptyFields() throws Exception {
    	//Tests with null fields
        Movie movie = new Movie(null, null, null, null); 
        
        mvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").value("Title cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.launchDate").value("Launch date cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.rating").value("Rating cannot be null"))
                .andExpect(jsonPath("$.fieldErrors.revenue").value("Revenue cannot be null"));            
        
    	//Test with empty title and null fields
        movie.setTitle("");
        mvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").value("Title cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.launchDate").value("Launch date cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.rating").value("Rating cannot be null"))
                .andExpect(jsonPath("$.fieldErrors.revenue").value("Revenue cannot be null"));
    }
    
    @Test
    void shouldNotCreateMovie_withInvalidLaunchDate() throws Exception {
    	Movie movie = new Movie(Long.valueOf(5L), "Movie", LocalDate.of(2030, 10, 10), new BigDecimal(8.0), Long.valueOf(678200000));
    	
    	mvc.perform(post("/api/movies")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(movie)))
    	        .andExpect(status().isBadRequest())
    	        .andExpect(jsonPath("$.fieldErrors.launchDate").value("Launch date must be in the past or present"));
    }
    
    @Test
    void shouldNotCreateMovie_withInvalidRating() throws Exception {
    	//Tests with a negative rating value
    	Movie movie = new Movie(Long.valueOf(5L), "Movie", LocalDate.of(1990, 10, 10), new BigDecimal(-0.1), Long.valueOf(678200000));
    	
    	mvc.perform(post("/api/movies")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(movie)))
    	        .andExpect(status().isBadRequest())
    	        .andExpect(jsonPath("$.fieldErrors.rating").value("Rating must be between 0.0 and 10.0"));
    	
    	//Tests with a larger than 10 rating value
    	movie.setRating(new BigDecimal(10.1));
    	mvc.perform(post("/api/movies")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(movie)))
    	        .andExpect(status().isBadRequest())
    	        .andExpect(jsonPath("$.fieldErrors.rating").value("Rating must be between 0.0 and 10.0"));
    }
    
    @Test
    void shouldCreateListOfValidMovies() throws Exception {
    	when(mockMovieService.createAll(anyList())).thenReturn(movies);
    	
        mvc.perform(post("/api/movies/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movies)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("application/json", result.getResponse().getContentType()))
                .andExpect(jsonPath("$[0].title").value(movies.get(0).getTitle()))
                .andExpect(jsonPath("$[1].title").value(movies.get(1).getTitle()))
                .andExpect(jsonPath("$[2].title").value(movies.get(2).getTitle()))
                .andExpect(jsonPath("$[3].title").value(movies.get(3).getTitle()));
                
        verify(mockMovieService).createAll(anyList());
    }
    
    @Test
    void shouldNotCreateListOfMovies_withEmptyAndNullFields() throws Exception {
	    //Tests with a list of movies with null and empty fields
		movies.forEach(m -> m.setTitle(""));
		movies.forEach(m -> m.setLaunchDate(null));
		movies.forEach(m -> m.setRating(null));
		movies.forEach(m -> m.setRevenue(null));
		
		mvc.perform(post("/api/movies/list")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movies)))
		        .andExpect(status().isBadRequest());
    }
    	
	@Test 
	void shouldNotCreateListOfMovies_withInvalidLaunchDates() throws Exception {
		movies.forEach(m -> m.setLaunchDate(LocalDate.of(2030, 10, 10)));
		
		mvc.perform(post("/api/movies/list")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movies)))
		        .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldNotCreateListOfMovies_withInvalidRatings() throws Exception {
		//Tests with negative ratings
		movies.forEach(m -> m.setRating(new BigDecimal(-0.1)));
		
		mvc.perform(post("/api/movies/list")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movies)))
		        .andExpect(status().isBadRequest());
		
		//Tests with ratings > 10
		movies.forEach(m -> m.setRating(new BigDecimal(10.1)));
		
		mvc.perform(post("/api/movies/list")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movies)))
		        .andExpect(status().isBadRequest());
	}
		

    @Test
    void shouldFindMovie_withValidId() throws Exception {
        Movie movie = movies.get(0);
        Long id = movie.getId();
        when(mockMovieService.findById(id)).thenReturn(movie);

        mvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("application/json", result.getResponse().getContentType()))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andExpect(jsonPath("$.launchDate").value(movie.getLaunchDate().toString()))
                .andExpect(jsonPath("$.rating").value(movie.getRating().doubleValue()))
                .andExpect(jsonPath("$.revenue").value(movie.getRevenue().longValue()));
        
        verify(mockMovieService).findById(id);

    }

    @Test
    void shouldNotFindMovie_withInvalidId() throws Exception {
    	List<Long> validIds = movies.stream()
    			                    .map(m -> m.getId())
    			                    .toList();
    	
    	Long invalidId = getRandomIdThatDoesNotExistInList(validIds);
    	
        when(mockMovieService.findById(invalidId)).thenThrow(new MovieNotFoundException(invalidId));
        
		mvc.perform(get("/api/movies/{id}", invalidId))
				.andExpect(status().isNotFound());
		
		verify(mockMovieService).findById(invalidId);
    }

    
    private Long getRandomIdThatDoesNotExistInList(List<Long> validIds) {
		Random r = new Random();
		Long newId;
		
		do {
			newId = r.nextLong();
			
		} while(validIds.contains(newId));
		
		return newId;
	}


	@Test
    void shouldFindAllMovies() throws Exception {
        when(mockMovieService.findAll()).thenReturn(movies);

        mvc.perform(MockMvcRequestBuilders.get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("application/json", result.getResponse().getContentType()))
                .andExpect(jsonPath("$[0].title").value(movies.get(0).getTitle()))
                .andExpect(jsonPath("$[1].title").value(movies.get(1).getTitle()))
                .andExpect(jsonPath("$[2].title").value(movies.get(2).getTitle()));
        
        verify(mockMovieService).findAll();
    }


    @Test
    void shouldFindMoviesByLaunchDate() throws Exception {
        LocalDate launchDate = LocalDate.of(1972, 10, 24);
        List<Movie> sameLaunchDateMovies = List.of(movies.get(2), movies.get(3));
        when(mockMovieService.findByLaunchDate(launchDate)).thenReturn(sameLaunchDateMovies);

        mvc.perform(get("/api/movies/launchDate/" + launchDate.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("application/json", result.getResponse().getContentType()))
                .andExpect(jsonPath("$[0].title").value(sameLaunchDateMovies.get(0).getTitle()))
                .andExpect(jsonPath("$[1].title").value(sameLaunchDateMovies.get(1).getTitle()));
        
        verify(mockMovieService).findByLaunchDate(launchDate);
    }

    @Test
    void shouldUpdateMovie_WithValidId() throws Exception {
    	Long oldMovieId = movies.get(3).getId();
        Movie newMovie = new Movie("Forrest Gump", LocalDate.of(1994, 10, 28), new BigDecimal(8.0), Long.valueOf(679835137));
        when(mockMovieService.update(any(Movie.class), anyLong())).thenReturn(new Movie(oldMovieId, newMovie.getTitle(), newMovie.getLaunchDate(), newMovie.getRating(), newMovie.getRevenue()));

        mvc.perform(put("/api/movies/{id}", oldMovieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("application/json", result.getResponse().getContentType()))
                .andExpect(jsonPath("$.id").value(oldMovieId))
                .andExpect(jsonPath("$.title").value(newMovie.getTitle()))
                .andExpect(jsonPath("$.launchDate").value(newMovie.getLaunchDate().toString()))
                .andExpect(jsonPath("$.rating").value(newMovie.getRating().doubleValue()))
                .andExpect(jsonPath("$.revenue").value(newMovie.getRevenue().longValue()));
        
        verify(mockMovieService).update(any(Movie.class), anyLong());
    }
    
    @Test
    void shouldNotUpdateMovie_WithInvalidId() throws Exception {
    	List<Long> validIds = movies.stream()
                                    .map(m -> m.getId())
                                    .toList();
    	
		Long invalidId = getRandomIdThatDoesNotExistInList(validIds);
		
		Movie newMovie = new Movie("Forrest Gump", LocalDate.of(1994, 10, 28), new BigDecimal(8.0), Long.valueOf(678200000));
		when(mockMovieService.update(any(Movie.class), eq(invalidId))).thenThrow(new MovieNotFoundException(invalidId));						
		
		mvc.perform(put("/api/movies/{id}", invalidId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newMovie)))
				.andExpect(status().isNotFound());
		
		verify(mockMovieService).update(any(Movie.class), eq(invalidId));
	}
    
    @Test
    void shouldNotUpdateMovie_withNullandEmptyFields() throws Exception {
    	//Tests with null fields
        Movie movie = new Movie(null, null, null, null);
        Long oldId = movies.get(3).getId();
        
        mvc.perform(put("/api/movies/{id}", oldId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").value("Title cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.launchDate").value("Launch date cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.rating").value("Rating cannot be null"))
                .andExpect(jsonPath("$.fieldErrors.revenue").value("Revenue cannot be null"));            
        
    	//Test with empty title and null fields
        movie.setTitle("");
        mvc.perform(put("/api/movies/{id}", oldId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").value("Title cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.launchDate").value("Launch date cannot be empty or null"))
                .andExpect(jsonPath("$.fieldErrors.rating").value("Rating cannot be null"))
                .andExpect(jsonPath("$.fieldErrors.revenue").value("Revenue cannot be null"));
    }
    
    
    @Test
    void shouldNotUpdateMovie_withInvalidLaunchDate() throws Exception {
        Movie movie = new Movie("Movie", LocalDate.of(2030, 10, 10), new BigDecimal(8.0), Long.valueOf(678200000));
        Long oldId = movies.get(3).getId();
        
    	mvc.perform(put("/api/movies/{id}", oldId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(movie)))
    	        .andExpect(status().isBadRequest())
    	        .andExpect(jsonPath("$.fieldErrors.launchDate").value("Launch date must be in the past or present"));
    }
    
    
    @Test
    void shouldNotUpdateMovie_withInvalidRating() throws Exception {
    	//Tests with a negative rating value
    	Movie movie = new Movie("Movie", LocalDate.of(1990, 10, 10), new BigDecimal(-0.1), Long.valueOf(678200000));
    	Long oldId = movies.get(3).getId();
    	
    	mvc.perform(put("/api/movies/{id}", oldId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(movie)))
    	        .andExpect(status().isBadRequest())
    	        .andExpect(jsonPath("$.fieldErrors.rating").value("Rating must be between 0.0 and 10.0"));
    	
    	//Tests with a larger than 10 rating value
    	movie.setRating(new BigDecimal(10.1));
    	mvc.perform(put("/api/movies/{id}", oldId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(movie)))
    	        .andExpect(status().isBadRequest())
    	        .andExpect(jsonPath("$.fieldErrors.rating").value("Rating must be between 0.0 and 10.0"));
    }
    
    @Test
    void shouldDeleteMovie() throws Exception {
        mvc.perform(delete("/api/movies/{id}", movies.get(0).getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteAllMovies() throws Exception {
        mvc.perform(delete("/api/movies"))
                .andExpect(status().isNoContent());
    }
}
