package ee.kaido.kmdb.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

//to make "02:30" to Duration
public class DurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext contextText) throws IOException {
        String durationString = jsonParser.getText();
        if (durationString.startsWith("PT")) {
            // ISO 8601 duration format (e.g., "PT10H30M")
            try {
                return Duration.parse(durationString);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid duration format: " + durationString);
            }
        } else if (durationString.contains(":")) {
            // "HH:mm" format (e.g., "10:30")
            String[] parts = durationString.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid duration format: " + durationString);
            }
            try {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return Duration.ofHours(hours).plusMinutes(minutes);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid duration format: " + durationString);
            }
        } else {
            try {
                long minutes = Long.parseLong(durationString);
                return Duration.ofMinutes(minutes);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid duration format: " + durationString);
            }
        }
    }
}