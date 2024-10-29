package ee.kaido.kmdb.deserializers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.service.GenreService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GenreListDeserializer extends JsonDeserializer<Set<Genre>> {
    private final GenreService genreService;

    public GenreListDeserializer(GenreService genreService) {
        this.genreService = genreService;
    }


    @Override
    public Set<Genre> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Set<Genre> genres = new HashSet<>();
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                //Check if its list of ids or actors
                Long genreIds = jsonNode.has("id") ? jsonNode.get("id").asLong() : jsonNode.asLong();
                try {
                    genres.add(genreService.getGenreByIdOrThrowError(genreIds));
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return genres;
    }
}
