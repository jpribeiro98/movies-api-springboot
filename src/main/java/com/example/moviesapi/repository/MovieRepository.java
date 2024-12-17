package com.example.moviesapi.repository;

import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.moviesapi.model.Movie;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class MovieRepository {

    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;
    
    public MovieRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Movie create(Movie movie) throws IllegalStateException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO Movie (title, launchDate, rating, revenue) VALUES (?,?,?,?)";
    	
        int nrRows = jdbcTemplate.update(connection -> {
        	PreparedStatement ps = connection
        			.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        	        ps.setString(1, movie.getTitle());
        	        ps.setObject(2, movie.getLaunchDate());
        	        ps.setBigDecimal(3, movie.getRating());
        	        ps.setLong(4, movie.getRevenue());
        	return ps;
        	
        }, keyHolder);
        	
        Number genKey = keyHolder.getKey();
        if (genKey != null) {
            movie.setId(genKey.longValue());
            
        } else {
            throw new IllegalStateException("Failed to retrieve generated ID for the movie " + movie.getTitle());
        }
        
        if(nrRows != 1) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException("INSERT INTO Movie", 1, nrRows);
		}
  
        return movie;
    }

    public List<Movie> createAll(List<Movie> movies) {
    	List<Movie> createdMovies = new ArrayList<>();
    	
        for (Movie movie : movies) {
			createdMovies.add(create(movie));
        }
        
        return createdMovies;
    }

    public Optional<Movie> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM Movie WHERE id = ?")
                         .param(id)
                         .query(Movie.class)
                         .optional();
    }

    public List<Movie> findAll() {
        return jdbcClient.sql("SELECT * FROM Movie")
                         .query(Movie.class)
                         .list();
    }

    public List<Movie> findByLaunchDate(LocalDate launchDate) {
        return jdbcClient.sql("SELECT * FROM Movie WHERE launchDate = ?")
                         .param(launchDate)
                         .query(Movie.class)
                         .list();
    }

    public Optional<Movie> update(Movie movie, Long id) {
        int nrRows = jdbcClient.sql("UPDATE Movie SET title = ?, launchDate = ?, rating = ?, revenue = ? WHERE id = ?")
                               .params(List.of(movie.getTitle(), movie.getLaunchDate(), movie.getRating(), movie.getRevenue(), id))
                               .update();
        
        if(nrRows != 1) {
			return Optional.empty();
		}

        return findById(id);
    }

    public void delete(Long id) {
        jdbcClient.sql("DELETE FROM Movie WHERE id = ?")
                  .param(id)
                  .update();
    }

    public void deleteAll() {
        jdbcClient.sql("DELETE FROM Movie").update();
    }
}
