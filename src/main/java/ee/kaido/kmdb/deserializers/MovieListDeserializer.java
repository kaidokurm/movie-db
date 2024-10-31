package ee.kaido.kmdb.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.MovieService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieListDeserializer extends JsonDeserializer<List<MovieDTO>> {
    private final MovieService movieService;

    public MovieListDeserializer(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public List<MovieDTO> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        List<MovieDTO> movieDTOS = new ArrayList<>();
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                //Check if its list of idNumber or movies
                Long movieId = jsonNode.has("id")
                        ? jsonNode.get("id").asLong()
                        : jsonNode.asLong();
                try {
                    movieDTOS.add(movieService.getMovieDtoById(movieId, false));
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return movieDTOS;
    }
}
