package com.example.moviesapi.exceptions;


public class MovieNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -3228437648256622269L;

	public MovieNotFoundException(Long id) {
		super("Movie with ID " + id + " was not found in the database");
	 }
}
