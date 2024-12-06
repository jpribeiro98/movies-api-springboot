package com.example.moviesapi.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorDetails {
	private final LocalDateTime timeStamp;
    private String title;
    private final int status;
    private String detail;
    private final String path;
    private Map<String,String> fieldErrors;

    
    public ErrorDetails(String title, int status, String detail, String path, Map<String, String> fieldErrors) {
    	this.timeStamp = LocalDateTime.now();
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }
    
    // Getters and Setters
    public LocalDateTime getTimeStamp() {
	    return timeStamp;
    }
    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
