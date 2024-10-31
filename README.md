# KMDB - Movie Database API

## Overview

KMDB is a robust REST API for managing a movie database, built using Spring Boot and JPA. This application allows users
to perform CRUD operations on movies, actors, and genres.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)

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

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080.
For more documentation go to http://localhost:8080/swagger-ui/index.html

For Postman is a ***Movie Database API.postman_collection.json*** file.

When needed to aad data run ***add_data.http***

# Usage

## Genre API Endpoints

## Overview

The Genre API provides endpoints for managing movie genres within the KMDB (Movie Database) application. Users can
create, read, update, and delete genres to organize and categorize movies effectively.

## API Endpoints

### 1. Add Genre

- **Method**: `POST`
- **Endpoint**: `/api/genres`
- **Request Body**:
    ```json
    {
      "name": "Genre Name"
    }
    ```
- **Response**:
    - **201 Created** with the details of the newly created genre.
    - **400 Bad request** with error message
- **Example Curl Command**:

  ```bash
      curl -X POST http://localhost:8080/api/genres \
      -H "Content-Type: application/json" \
      -d '{"name": "Action"}'
  ```

### 2. Get Genre by ID

- **Method**: `GET`
- **Endpoint**: `/api/genres/{id}`
- **Response**:
    - **200 OK** with the details of the requested genre or null if there is non.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/genres/1
  ```

### 3. Get All Genres

- **Method**: `GET`
- **Endpoint**: `/api/genres`
- **Response**:
    - **200 OK** with a list of all genres.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/genres
  ```

### 4. Get Genre Movies

- **Method**: `GET`
- **Endpoint**: `/api/genres/{id}/movies`
- **Optional Parameter**: `?showActors=true` to show actors
- **Response**:
    - **200 OK** with the movies.
    - **404 Not Found** if the genre does not exist.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/genres/1/movies
  ```

### 5. Update Genre

- **Method**: `PATCH`
- **Endpoint**: `/api/genres/{id}`
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
- **Example Curl Command**:
  ```bash
    curl -X PATCH http://localhost:8080/api/genres/1 \
    -H "Content-Type: application/json" \
    -d '{"name": "Adventure"}'
  ```

### 6. Delete Genre

- **Method**: `DELETE`
- **Endpoint**: `/api/genres/{id}`
- **Optional Endpoint**: `?force=true`
- **Response**:
    - **204 No Content** if the genre was successfully deleted.
    - **400 Bad Request** if force is false and there exist associated movies
    - **404 Not Found** if the genre was not found.
- **Example Curl Command**:
  ```bash
    curl -X DELETE http://localhost:8080/api/genres/1
  ```

# Actor API Endpoints

## Overview

The Actor API provides endpoints for managing actors within the KMDB (Movie Database) application. Users can create,
read, update, and delete actors to maintain a comprehensive database of individuals associated with movies.

## API Endpoints

### 1. Add Actor

- **Method**: `POST`
- **Endpoint**: `/api/actors`
- **Request Body**:
    ```json
    {
      "name": "Actor Name",
      "birthdate": "yyyy-MM-dd",
      "movies": [{"id": 1 },{"id": 2}] or [1,2]
    }
    ```
- **Response**:
    - **201 Created** with the details of the newly created actor.
    - **400 Bad Request** wrong date or bad name.
    - **404 Not Found** if the movie with id does not exist.
- **Example Curl Command**:
  ```bash
  curl -X POST http://localhost:8080/api/actors \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Doe", "birthDate": "1980-01-01",
  "movies":{1,{"id":2}'
  ```

### 2. Get Actor by ID

- **Method**: `GET`
- **Endpoint**: `/api/actors/{id}`
- **Optional Parameter**: `?showMovies=false` to hide movies
- **Response**:
    - **200 OK** with the details of the requested actor.
    - **404 Not Found** if the actor does not exist.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/actors/1
  ```

### 3. Get All Actors

- **Method**: `GET`
- **Endpoint**: `/api/actors`
- **Optional Parameters**:
    - **`showMovies=false`** to hide movie details
    - **`name=Name`** filter by name
    - **`page=0&size=10`** for pageable
- **Response**:
    - **200 OK** with a list of all actors.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/actors
  ```

### 4. Get All Actor Movies

- **Method**: `GET`
- **Endpoint**: `/api/actors/{id}/movies`
- **Response**:
    - **200 OK** with a list of all actors.
    - **404 Not Found** if the actor does not exist.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/actor/1/movies
  ```

### 5. Update Actor

- **Method**: `PATCH`
- **Endpoint**: `/api/actors/{id}`
- **Request Body**:
    ```json
    {
      "name": "Updated Actor Name",
      "birthdate": "YYYY-MM-DD",
      "movies": [{Updated movie 1},{Updated movie 2}...] or [1,2]
    }
    ```
- **Response**:
    - **200 OK** with the updated actor details.
    - **400 Bad Request** wrong input.
    - **404 Not Found** if the actor does not exist.
- **Example Curl Command**:
  ```bash
  curl -X PATCH http://localhost:8080/api/actor/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane", "birthdate": "1980-01-01", "movies": [{"id":1},2]}'
  ```

### 6. Delete Actor

- **Method**: `DELETE`
- **Endpoint**: `/api/actors/{id}`
- **Optional**: `?force=true`
- **Response**:
    - **204 No Content** if the actor was successfully deleted.
    - **400 Bad Request** if force is false and there exist associated movies
    - **404 Not Found** if the actor does not exist.
- **Example Curl Command**:
  ```bash
  curl -X DELETE http://localhost:8080/api/actor/1
  ```

# Movie API Endpoints

## Overview

The Movie API provides endpoints for managing movies within the KMDB (Movie Database) application. Users can create,
read, update, and delete movies, as well as manage their associated actors and genres.

## API Endpoints

### 1. Add Movie

- **Method**: `POST`
- **Endpoint**: `/api/movies`
- **Request Body**:
    ```json
    {
      "title": "Movie Title",
      "releaseYear": 2023,
      "duration": "PT1H10M or 70 or 01:10",
      "genres": [{"id": 1},{"id": 2},
                or 3,4],
      "actors": [{"id": 1},{"id": 2},
                or 1,2]
    }
    ```

- **Response**:
    - **201 Created** with Movie details.
    - **400 Bad Request**
    - **404 Not Found** if actor or genre does not exist.
- **Example Curl Command**:
  ```bash
  curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{"title": "Inception", "releaseYear": 2010, "duration":"01:10", 
  "genres": [1,{"id":2}], "actors": [{"id":1}, 2]}'
  ```

### 2. Get Movie by ID

- **Method**: `GET`
- **Endpoint**: `/api/movies/{id}`
- **Optional Parameter**: `showActors=false` default true
- **Response**:
    - **200 OK** with Movie details.
    - **404 Not Found** no Movie found.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/movies/1
  ```

### 3. Get Movies by Filter

- **Method**: `GET`
- **Endpoint**: `/api/movies` or `/api/movies/search`
- **Query Parameters**:
    - `genreId=1` (optional)
    - `releaseYear=1982` (optional)
    - `actorId=1` (optional)
    - `title=movie title` (optional)
    - `page=0` (optional)
    - `size=10` (optional)
    - `showActors=false` (optional)

* Paginator works only if Page (0...) and Size (1...) both exist and are valid

- **Response**:
    - **200 OK** with a list of MovieDTO.
    - **404 Not Found** if actor or genre does not exist.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/movies
  ```

### 4. Get Actors by Movie ID

- **Method**: `GET`
- **Endpoint**: `/api/movies/{movieId}/actors`
- **Response**:
    - **200 OK** with a list of ActorDTO.
    - **404 Not Found** if movie does not exist.
- **Example Curl Command**:
  ```bash
  curl -X GET http://localhost:8080/api/movies/1/actors
  ```

### 5. Update Movie

- **Method**: `PATCH`
- **Endpoint**: `/api/movies/{id}`
- **Request Body**:
    ```json
    {
      "title": "Movie Title",
      "releaseYear": 2023,
      "duration": "PT1H10M or 70 or 01:10",
      "genres": [
        {"id": 1},
        {"id": 2},
        3,
        4
      ],
      "actors": [
        {"id": 1},
        {"id": 2},
        3,
        4
      ]
    }
    ```

* id is needed and json can have fields that are changing

- **Response**:
    - **200 OK** with updated Movie details.
    - **400 Bad Request** error in input
    - **404 Not Found** no data found
- **Example Curl Command**:
  ```bash
  curl -X PUT http://localhost:8080/api/movies/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Inception", "releaseYear": 2010, "genres": [2,3], "actors": [1,2]}'
  ```

### 6. Delete Movie

- **Method**: `DELETE`
- **Endpoint**: `/api/movies/{id}`
- **Response**:
    - **204 No Content** if the movie was successfully deleted.
    - **404 Not Found** if movie does not exist.
- **Example Curl Command**:
  ```bash
  curl -X DELETE http://localhost:8080/api/movies/1
  ```
