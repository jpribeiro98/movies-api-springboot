# Movies API

This is a small RESTful API for managing a collection of movies, providing basic CRUD operations on Movie resources. This project does not include a front-end interface, focusing entirely on the back-end development and API functionality. It was implemented using **Spring Boot** with an embedded H2 database for data storage.


## Project Requirements:

- **Java Version**: 17 or higher
- **Build Tool**: Maven 3.6.3 or higher. You can check your installed Maven version by running the following command:

```bash
  mvn --version
```


## How to Run the Application

I have already built the JAR file so to run the application just follow these simple steps:

**1. Navigate to the target directory**. Open your command prompt, and navigate to the target directory where the JAR file is located.

```bash 
cd path\to\your\project\target
```
   
**2. Run the JAR file**. Use the following command to start the application:

```bash
java -jar moviesapi-0.0.1-SNAPSHOT.jar
````
   
**3. Access the application**. The server should now be running on: ``http://localhost:8080``


## Movie Resource

Each movie in the application has the following attributes:

- **id** (Long): A unique identifier for the movie, automatically generated by the database.
- **title** (String): The title of the movie.
- **launch date** (LocalDate): The release date of the movie in the format yyyy-MM-dd.
- **rating** (BigDecimal): The movie's rating, ranging from 0.0 to 10.0, allowing up to one digit after the decimal point.
- **revenue** (Long): The total revenue of the movie in USD.

Movie Resource Example (JSON):

```JSON 
{
  "title": "Inception",
  "launchDate": "2010-07-16",
  "rating": 8.8,
  "revenue": 825786894
}
```


## API Endpoints

Here are the endpoints for interacting with the API, categorized by their functionality:

### 1. Get All Movies
- **GET** /api/movies
- Retrieves a list with all the movies in the database.

Example:
- **GET** ``http://localhost:8080/api/movies``
  

### 2. Get Movies by launch date
- **GET** /api/movies/launchDate/{date}
- Retrieves a list of movies with the same launch date.

Example:
- **GET** ``http://localhost:8080/api/movies/launchDate/1994-10-14``


### 3. Get a Movie by id
- **GET** /api/movies/{id}
- Retrieves a specific movie identified by its unique ID.

Example:
- **GET** ``http://localhost:8080/api/movies/1``
 

### 4. Add a New Movie
- **POST** /api/movies
- Adds a new movie to the database.

Example:
- **POST** ``http://localhost:8080/api/movies``
- Content-Type: application/json
- Body:  

```JSON 
{
  "title": "Inception",
  "launchDate": "2010-07-16",
  "rating": 8.8,
  "revenue": 825786894
}
```
          
          
### 5. Add all movies from a list
- **POST** /api/movies/list
- Adds a list of movies to the database.

Example:
- **POST** ``http://localhost:8080/api/movies/list``
- Content-Type: application/json
- Body:

```JSON
[
  {
    "title": "Pulp Fiction",
    "launchDate": "1994-10-14",
    "rating": 8.9,
    "revenue": 212891598
  },
  {
    "title": "The Shawshank Redemption",
    "launchDate": "1994-09-23",
    "rating": 9.3,
    "revenue": 28713353
  },
  {
    "title": "The Godfather",
    "launchDate": "1972-03-24",
    "rating": 9.2,
    "revenue": 270007394
  }
]
```

### 6. Update a movie by id
- **PUT** /api/movies/{id}
- Updates the movie identified by its unique ID using the details of the provided movie.

Example:
- **PUT** ``http://localhost:8080/api/movies/1``
- Content-Type: application/json
- Body:

```JSON
{
  "title": "Inception updated",
  "launchDate": "2010-07-16",
  "rating": 9.0,
  "revenue": 825786894
}
```

### 7. Delete a Movie by id
- **DELETE** /api/movies/{id}
- Removes the movie identified by its unique ID from the database.

Example:
- **DELETE** ``http://localhost:8080/api/movies/2``


### 8. Delete all movies
- **DELETE** /api/movies
- Removes all the movies from the database.

Example:
- **DELETE** ``http://localhost:8080/api/movies``


## Error Handling

This API includes comprehensive error-handling mechanisms implemented via a `GlobalExceptionHandler` class to provide meaningful responses for various error scenarios. The errors are represented in a `ErrorDetails` class and have the following attributes:

- **timestamp** (LocalDateTime): The date and time when the error occurred in the ISO-8601 calendar system.
- **title** (String): A short description of the error.
- **status** (int): The HTTP status code associated with the error. Handled HTTP error status codes: `400 Bad Request`, `404 Not Found`, `405 Method Not Allowed`, `500 Internal Server Error`.
- **detail** (String): A more detailed explanation of the error.
- **path** (String): The URI where the error occurred.
- **fieldErrors** (Map<String,String>): Specific field validation errors (if applicable).


## Common Error Scenarios:

### 1. Validation Errors in the request body
Occurs when input data for a movie or a list of movies is invalid.

**Example Scenario:** The client sends a POST request to `/api/movies` with a request body containing a movie where the launchDate  is set to a future date (e.g., "launchDate": "2025-10-12") and the rating is a negative number (e.g., "rating": -0.1). Since launch dates in the future and negative ratings are invalid, the API identifies these errors and returns the following error response:

```JSON
{
  "timestamp": "2024-12-05T15:45:30.462511",
  "title": "Validation Errors",
  "status": 400,
  "detail": "Invalid fields in the provided movie",
  "path": "/api/movies",
  "fieldErrors": {
      "launchDate": "Launch date must be in the format yyyy-MM-dd"
      "rating": "Rating must be between 0.0 and 10.0"
  }
}
```

### 2. Validation Error in the Path Variable





### 3. Incorrect Request Format
Occurs when the request data does not match the required structure or contains fields with invalid types or formats.

**Example Scenario:** The client sends a POST request to `/api/movies` with a request body containing a movie where the launchDate has an invalid format (e.g., "launchDate": "1994-28"). The API detects the incorrect format and returns the following error response:

```JSON
{
  "timestamp": "2024-12-05T15:50:30.122411",
  "title": "Request Format Errors",
  "status": 400,
  "detail": "The provided movie has fields with incorrect formats",
  "path": "/api/movies",
  "fieldErrors": {
      "launchDate": "Launch date must be in the format yyyy-MM-dd"
  }
}
```

### 4. Path Variable in the Wrong Format
Occurs when a path variable provided in the request does not match the expected type or format.

**Example Scenario:** The client sends a GET request to `/api/movies/abc`, but the id path variable is expected to be a numeric value. Since "abc" is not a valid number, the API detects the type mismatch and returns the following error response:

```JSON
{
  "timeStamp": "2024-12-05T15:55:30.422411",
  "title": "ID path variable is in the wrong format",
  "status": 400,
  "detail": "ID must be a numeric value between -9223372036854775808 and 9223372036854775807",
  "path": "/api/movies/abc",
  "fieldErrors": null
}
```

### 5. Movie Not Found
Occurs when the requested movie cannot be found in the database.

**Example Scenario:** The client sends a GET request to `/api/movies/99` to retrieve details for a movie with ID 99. Since no movie with this ID exists in the database, the API returns the following error message:

```JSON
{
  "timestamp": "2024-12-05T15:56:30.232211",
  "title": "Movie Not Found",
  "status": 404,
  "detail": "Movie with ID 99 was not found in the database",
  "path": "/api/movies/99",
  "fieldErrors": null
}
```

### 6. Invalid URI




### 7. Method Not Allowed
Occurs when using an unsupported HTTP method for a specific endpoint.

**Example Scenario:** The client sends a PUT request to `/api/movies` with a movie in the request body. However this endpoint only supports POST, GET and DELETE methods. The API detects the invalid method and returns the following error response:

```JSON
{
  "timestamp": "2024-12-05T15:58:30.323411",
  "title": "Method Not Allowed",
  "status": 405,
  "detail": "The request method PUT is not allowed for this endpoint. Allowed methods for this endpoint: POST, GET, DELETE",
  "path": "/api/movies",
  "fieldErrors": null
}
```

### 8. Internal Server Error





## Running the JUnit Tests

To run the JUnit tests for this project:

**1.** Inside the root directory of your project, run the following Maven command:

```bash
mvn test 
```

**2.** This will execute the JUnit tests, ensuring the functionality of the core components of the application, including:
- `MovieRepository`
- `MovieService`
- `MovieController`


## Conclusion

Working on this project has been an incredible learning experience. Along the way, I’ve gained a deeper understanding of Spring Boot's architecture and its powerful features for building scalable and maintainable RESTful APIs. I’ve honed my skills in designing and implementing API endpoints, managing application configurations, and using Spring Boot's dependency injection to simplify development. I also learned how to work with embedded databases like H2, using the JDBC API for database operations which gave me a clearer picture of how data persistence works in modern applications, from setting up schemas and running queries to troubleshooting database issues. It has also helped me develop a stronger grasp of backend development best practices, including error handling, testing, and optimizing performance. 

Working with Spring Boot has shown me how its simplicity and flexibility make it a great tool for reducing repetitive tasks and creating a better development experience.

<br>
<br>

### Contact Information:

- <span style="font-size: 20px vertical-align: middle;">📞</span> +351 926 013 231
- <img src="https://upload.wikimedia.org/wikipedia/commons/8/8c/Gmail_Icon_%282013-2020%29.svg" alt="Gmail" width="20" height="20" style="vertical-align: middle;"> [Email](mailto:jpgr1998@gmail.com)
- <img src="https://upload.wikimedia.org/wikipedia/commons/7/7e/LinkedIn_PNG16.png" alt="LinkedIn" width="20" height="20" style="vertical-align: middle;"> [LinkedIn profile](https://www.linkedin.com/in/joaopedrogoncalvesribeiro/)

<br>
<br>

**Developed by João Pedro Gonçalves Ribeiro**