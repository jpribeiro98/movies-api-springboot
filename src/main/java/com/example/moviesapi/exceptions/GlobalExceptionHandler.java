package com.example.moviesapi.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;




@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles a validation exception for requests with a single movie in its body(400 BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorDetails errorDetails = new ErrorDetails(
                "Validation Error", // Title of the error
                HttpStatus.BAD_REQUEST.value(), // Status code (400)
                "Invalid fields in the provided movie", // Detail message  
                request.getDescription(false).replace("uri=", ""), // URI of the request
                fieldErrors // Map of field-specific errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    //Handles a validation exception for requests with a list of movies in its body or
    //requests with an invalid path variable launchDate (400 BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorDetails> handleListValidationException(HandlerMethodValidationException ex, WebRequest request) {
    	Map<String, String> fieldErrors = new HashMap<>();
    	String errorDetail = "Invalid fields in the request";
    	
    	if(ex.getMethod().getName().equals("findByLaunchDate")) {
    		errorDetail = "Invalid path variable launch date";
    		fieldErrors.put("launchDate", "Launch date must be in the past or present");
    		
    	} else if(ex.getMethod().getName().equals("createAll")) {
    		errorDetail = "Invalid fields in the provided movie list";
    		
    		for(MessageSourceResolvable error: ex.getAllErrors()) {
            	FieldError fieldError = (FieldError) error;
    			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    		}
    	}
    	
    	ErrorDetails errorDetails = new ErrorDetails(
                "Validation Error", // Title of the error
                HttpStatus.BAD_REQUEST.value(), // Status code (400)
                errorDetail, // Detail message  
                request.getDescription(false).replace("uri=", ""), // URI of the request
                fieldErrors // Map of field-specific errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
	    
    //Handles a incorrect request format exception (400 BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ErrorDetails errorDetails = null;
        
        if(!ex.getMessage().contains("LocalDate") && !ex.getMessage().contains("BigDecimal")
        		&& !ex.getMessage().contains("Long")) {
        	
        	// Prepare the error details
            errorDetails = new ErrorDetails(
            		"Incorrect Request Format", // Title of the error
                    HttpStatus.BAD_REQUEST.value(), // Status code (400)
                    ex.getMessage(), // Detail message
                    request.getDescription(false).replace("uri=", ""), // URI of the request
                    fieldErrors // Map of field-specific errors
                    );
        } else {
        	if(ex.getMessage().contains("LocalDate")) {
        		fieldErrors.put("launchDate", "Launch date must be in the format yyyy-MM-dd");
        	}
        	
			if(ex.getMessage().contains("BigDecimal")) {
				fieldErrors.put("rating", "Rating must be a number between 0.0 and 10.0");
			}
			
			if(ex.getMessage().contains("Long")) {
				fieldErrors.put("revenue", "Revenue must be a number");
			}
		
	        errorDetails = new ErrorDetails(
	        		"Incorrect Request Format", // Title of the error
	                HttpStatus.BAD_REQUEST.value(), // Status code (400)
	                "Incorrect format for the movie fields", // Detail message
	                request.getDescription(false).replace("uri=", ""), // URI of the request
	                fieldErrors // Map of field-specific errors
	                );
        }      
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    //Handles a type mismatch exception for path variables in the wrong format (400 BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
    	String errorTitle = "Path variable in the wrong format";
    	String errorDetail = "The path variable is in the wrong format";
    	
    	//invalid id
    	if(ex.getMessage().contains("Method parameter 'id'")) {   		
    		errorTitle = "Path variable ID in the wrong format";
    		errorDetail = "The ID must be a numeric value between -9223372036854775808 and 9223372036854775807";   		
    	
        //invalid launchDate
    	} else if (ex.getMessage().contains("Method parameter 'launchDate'")) {
    		errorTitle = "Path variable launch date in the wrong format";
    		errorDetail = "Launch date must be in the format yyyy-MM-dd";   		
    	}
    	
    	ErrorDetails errorDetails = new ErrorDetails(
                errorTitle, // Title of the error
                HttpStatus.BAD_REQUEST.value(), // Status code (400)
                errorDetail, // Detail message
                request.getDescription(false).replace("uri=", ""), // URI of the request
                null // Map of field-specific errors
        );
    	
    	return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handles a movie not found exception (404 NOT_FOUND)
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleMovieNotFoundException(MovieNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                "Movie Not Found", // Title of the error
                HttpStatus.NOT_FOUND.value(), // Status code (404)
                ex.getMessage(), // Detail message
                request.getDescription(false).replace("uri=", ""), // URI of the request
                null // Map of field-specific errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    //Handles a no resource found exception thrown when the request has an invalid URI (404 NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleMissingPathVariable(NoResourceFoundException ex, WebRequest request) {
    	ErrorDetails errorDetails = new ErrorDetails(
                "Resource Not Found", // Title of the error
                HttpStatus.NOT_FOUND.value(), // Status code (404)
                "The requested resource could not be found. Please ensure your request URI has the correct endpoint for acessing that resource", // Detail message
                request.getDescription(false).replace("uri=", ""), // URI of the request
                null // Map of field-specific errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

	// Handles a method not supported exception (405 METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                "Method Not Allowed", // Title of the error
                HttpStatus.METHOD_NOT_ALLOWED.value(), // Status code (405)
                String.format("The request method '%s' is not allowed for this endpoint. Allowed methods: %s", 
                    ex.getMethod(), // Get the method that was used
                    ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods()) : "None available"), // Supported methods
                request.getDescription(false).replace("uri=", ""), // URI of the request
                null // Map of field-specific errors
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }
   
    // Handles other generic exceptions (500 INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception ex, WebRequest request) {
    	ErrorDetails errorDetails = new ErrorDetails(
                "Internal Server Error", // Title of the error
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // Status code (404)
                ex.getMessage(), // Detail message
                request.getDescription(false).replace("uri=", ""), // URI of the request
                null // Map of field-specific errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
