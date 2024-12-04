package com.example.moviesapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public class Movie {
	
    private Long id;

    @NotEmpty(message = "Title cannot be empty or null")
    private String title;

    @NotNull(message = "Launch date cannot be empty or null")
    @PastOrPresent(message = "Launch date must be in the past or present")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate launchDate;//yyyy-MM-dd format

    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be between 0.0 and 10.0")
    private BigDecimal rating; //from 0.0 to 10.0 with up to 1 digit after the decimal point.

    @NotNull(message = "Revenue cannot be null")
    private Long revenue; //in USD
    
    protected Movie() {}
    
    public Movie(String title, LocalDate launchDate, BigDecimal rating, Long revenue) {
		this.title = title;
		this.launchDate = launchDate;
		this.rating = rating;
		this.revenue = revenue;
	}
    
    public Movie(Long id, String title, LocalDate launchDate, BigDecimal rating, Long revenue) {
		this.id = id;
		this.title = title;
		this.launchDate = launchDate;
		this.rating = rating;
		this.revenue = revenue;
	}
    
    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Long getRevenue() {
        return revenue;
    }

    public void setRevenue(Long revenue) {
        this.revenue = revenue;
    } 
}
