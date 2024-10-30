package ee.kaido.kmdb.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.service.ActorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActorListDeserializer extends JsonDeserializer<List<ActorDTO>> {
    private final ActorService actorService;

    public ActorListDeserializer(ActorService actorService) {
        this.actorService = actorService;
    }

    @Override
    public List<ActorDTO> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        List<ActorDTO> actorIds = new ArrayList<>();
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                //Check if its list of ids or actors
                Long actorId = jsonNode.has("id") ? jsonNode.get("id").asLong() : jsonNode.asLong();
                try {
                    actorIds.add(new ActorDTO(actorId, actorService));
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return actorIds;
    }
}
