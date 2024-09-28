package ee.kaido.kmdb.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

//to make "02:30" to duration
public class DurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String durationString = jsonParser.getText();
        if (durationString.startsWith("PT")) {
            // ISO 8601 duration format (e.g., "PT10H30M")
            return Duration.parse(durationString);
        } else {
            // "HH:mm" format (e.g., "10:30")
            String[] parts = durationString.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return Duration.ofHours(hours).plusMinutes(minutes);
        }
    }
}