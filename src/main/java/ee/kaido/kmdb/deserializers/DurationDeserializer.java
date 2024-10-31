package ee.kaido.kmdb.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.time.Duration;

//to make "02:30" "PT1H10M" "120" to Duration
public class DurationDeserializer extends JsonDeserializer<Duration> {

    private static final String NEGATIVE_DURATION_ERROR_MESSAGE = "Duration cannot be negative";
    private static final String INVALID_FORMAT_ERROR_MESSAGE = "Invalid duration format: ";


    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext contextText)
            throws IOException {
        String durationString = jsonParser.getText();
        Duration duration;
        try {
            if (durationString.startsWith("PT")) {
                duration = getDuration(durationString);
            } else if (durationString.contains(":")) {
                duration = getDurationFromTime(durationString);
            } else {
                duration = getDurationFromMinutes(durationString);
            }
            ifDurationIsNegativeThrowError(duration);
            return duration;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_FORMAT_ERROR_MESSAGE + durationString);
        }
    }

    private static Duration getDuration(String durationString) {
        Duration duration;
        // ISO 8601 duration format (e.g., "PT10H30M")
        duration = Duration.parse(durationString);
        return duration;
    }

    private static Duration getDurationFromTime(String durationString) {
        Duration duration;
        // "HH:mm" format (e.g., "10:30")
        String[] parts = durationString.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException(INVALID_FORMAT_ERROR_MESSAGE
                    + durationString);
        }
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        duration = Duration.ofHours(hours).plusMinutes(minutes);
        return duration;
    }

    private static Duration getDurationFromMinutes(String durationString) {
        Duration duration;
        long minutes = Long.parseLong(durationString);
        duration = Duration.ofMinutes(minutes);
        return duration;
    }


    private static void ifDurationIsNegativeThrowError(Duration duration)
            throws BadRequestException {
        if (duration.isNegative()) {
            throw new BadRequestException(NEGATIVE_DURATION_ERROR_MESSAGE);
        }
    }
}