# KMDB - Movie Database API

## Overview

KMDB is a robust REST API for managing a movie database, built using Spring Boot and JPA. This application allows users
to perform CRUD operations on movies, actors, and genres.

## Features

- **Actor Management**: Create, read, update, and delete actors.
- **Genre Management**: Create, read, update, and delete genres.
- **Movie Management**: Create, read, update, and delete movies.
- **Filtering**: Filter actors and movies by various criteria.

## Technologies Used

- **Java**: 21
- **Spring Boot**: 3.3.4
- **Spring Data JPA**
- **SQLite**: For persistent database storage.
- **Lombok**: For reducing boilerplate code.

## Setup Instructions

### Prerequisites

- **Java 21** or higher
- **Maven**: Ensure Maven is installed on your machine.
- **SQLite**: Ensure you have SQLite installed. You can download it from
  the [SQLite official website](https://www.sqlite.org/download.html).

### Clone the Repository

```bash
git clone https://gitea.kood.tech/kaidokurm/kmdb.git
cd kmdb
```

### Build the Project

```bash
mvn clean install
```

### Configure SQLite

1. Database File: By default, SQLite creates a database file in the current directory. You can specify a different
   location in the application.properties file.

2. Configure application.properties: Update your src/main/resources/application.properties file to configure the SQLite
   datasource:

```properties
spring.datasource.url=jdbc:sqlite:kmdb.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080.
For more documentation go to http://localhost:8080/swagger-ui/index.html

# Usage

# Genre API Endpoints

## Overview

The Genre API provides endpoints for managing movie genres within the KMDB (Movie Database) application. Users can
create, read, update, and delete genres to organize and categorize movies effectively.

## API Endpoints

### 1. Add Genre

- **Method**: `POST`
- **Endpoint**: `/api/genre`
- **Request Body**:
    ```json
    {
      "name": "Genre Name"
    }
    ```
- **Response**:
    - **201 Created** with the details of the newly created genre.

### 2. Get Genre by ID

- **Method**: `GET`
- **Endpoint**: `/api/genre/{id}`
- **Response**:
    - **200 OK** with the details of the requested genre or null if there is non.

### 3. Get All Genres

- **Method**: `GET`
- **Endpoint**: `/api/genres`
- **Response**:
    - **200 OK** with a list of all genres.

### 4. Get Genre Movies

- **Method**: `GET`
- **Endpoint**: `/api/genre/{id}/movies`
- **Response**:
    - **200 OK** with the movies.
    - **404 Not Found** if the genre does not exist.

### 5. Update Genre

- **Method**: `PATCH`
- **Endpoint**: `/api/genre/{id}`
- **Request Body**:
    ```json
    {
      "name": "Updated Genre Name"
    }
    ```
- **Response**:
    - **200 OK** with the updated genre details.
    - **404 Not Found** if the genre does not exist.
    - **406 Not Acceptable** if the genre name already exists.

### 6. Delete Genre

- **Method**: `DELETE`
- **Endpoint**: `/api/genre/{id}`
- **Optional Endpoint**: `/api/gente/{id}?force=true`
- **Response**:
    - **204 No Content** if the genre was successfully deleted.
    - **400 Bad Request** if force is false and there exist associated movies
    - **404 Not Found** if the genre does not exist.

# Actor API Endpoints

## Overview

The Actor API provides endpoints for managing actors within the KMDB (Movie Database) application. Users can create,
read, update, and delete actors to maintain a comprehensive database of individuals associated with movies.

## API Endpoints

### 1. Add Actor

- **Method**: `POST`
- **Endpoint**: `/api/actor`
- **Request Body**:
    ```json
    {
      "name": "Actor Name",
      "birthdate": "YYYY-MM-DD",
      "movies": [{"id": 1 },{"id": 2}]
    }
    ```
- **Response**:
    - **201 Created** with the details of the newly created actor.
    - **404 Not Found** if the movie with id does not exist.

### 2. Get Actor by ID

- **Method**: `GET`
- **Endpoint**: `/api/actor/{id}`
- **Response**:
    - **200 OK** with the details of the requested actor.
    - **404 Not Found** if the actor does not exist.

### 3. Get All Actors

- **Method**: `GET`
- **Endpoint**: `/api/actors`
- **Optional filters** `?showMovies=false&name=Name`
- **Response**:
    - **200 OK** with a list of all actors.

### 4. Get All Actor Movies

- **Method**: `GET`
- **Endpoint**: `/api/actors/{id}/movies`
- **Response**:
    - **200 OK** with a list of all actors.
    - **404 Not Found** if the actor does not exist.

### 5. Update Actor

- **Method**: `PATCH`
- **Endpoint**: `/api/actor/{id}`
- **Request Body**:
    ```json
    {
      "name": "Updated Actor Name",
      "birthdate": "YYYY-MM-DD",
      "movies": [Updated movie 1,Updated movie 2...]
    }
    ```
- **Response**:
    - **200 OK** with the updated actor details.
    - **404 Not Found** if the actor does not exist.

### 6. Delete Actor

- **Method**: `DELETE`
- **Endpoint**: `/api/actor/{id}`
- **Optional**: `?force=true`
- **Response**:
    - **204 No Content** if the actor was successfully deleted.
    - **400 Bad Request** if force is false and there exist associated movies
    - **404 Not Found** if the actor does not exist.

# Movie API Endpoints

## Overview

The Movie API provides endpoints for managing movies within the KMDB (Movie Database) application. Users can create,
read, update, and delete movies, as well as manage their associated actors and genres.

## API Endpoints

### 1. Add Movie

- **Method**: `POST`
- **Endpoint**: `/api/movie`
- **Request Body**:
    ```json
    {
      "title": "Movie Title",
      "releaseYear": 2023,
      "duration": "PT1H10M or 70 or 01:10",
      "genres": [
        {"id": 1},
        {"id": 2}
      ],
      "actors": [
        {"id": 1},
        {"id": 2}
      ]
    }
    ```

- **Response**:
    - **201 Created** with Movie details.
    - **404 Not Found** if actor or genre does not exist.

### 2. Get Movie by ID

- **Method**: `GET`
- **Endpoint**: `/api/movie/{id}`
- **Response**:
    - **200 OK** with Movie details.

### 3. Get Movies by Filter

- **Method**: `GET`
- **Endpoint**: `/api/movies` or `/api/movies/search`
- **Query Parameters**:
    - `genreId` (optional)
    - `releaseYear` (optional)
    - `actorId` (optional)
    - `title` (optional)
    - `page` (optional)
    - `size` (optional)

* Paginator works only if Page (0...) and Size (1...) both exist and are valid

- **Response**:
    - **200 OK** with a list of MovieDTO.
    - **404 Not Found** if actor or genre does not exist.

### 4. Get Actors by Movie ID

- **Method**: `GET`
- **Endpoint**: `/api/movies/{movieId}/actors`
- **Response**:
    - **200 OK** with a list of ActorDTO.
    - **404 Not Found** if movie does not exist.

### 5. Update Movie

- **Method**: `PATCH`
- **Endpoint**: `/api/movie/{id}`
- **Request Body**:
    ```json
    {
      "title": "Movie Title",
      "releaseYear": 2023,
      "duration": "PT1H10M or 70 or 01:10",
      "genres": [
        {"id": 1},
        {"id": 2}
      ],
      "actors": [
        {"id": 1},
        {"id": 2}
      ]
    }
    ```

- **Response**:
    - **200 OK** with updated Movie details.

### 6. Delete Movie

- **Method**: `DELETE`
- **Endpoint**: `/api/movie/{id}`
- **Response**:
    - **204 No Content** if the movie was successfully deleted.
    - **404 Not Found** if movie does not exist.