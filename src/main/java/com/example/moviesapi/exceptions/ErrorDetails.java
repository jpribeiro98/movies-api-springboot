package com.example.moviesapi.exceptions;

import java.util.Map;

public class ErrorDetails {
    private String title;
    private int status;
    private String detail;
    private String path;
    private Map<String, String> fieldErrors;


    // Constructors
    public ErrorDetails() {}

    public ErrorDetails(String title, int status, String detail, String path, Map<String, String> fieldErrors) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
