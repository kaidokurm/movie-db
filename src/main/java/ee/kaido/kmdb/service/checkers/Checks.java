package ee.kaido.kmdb.service.checkers;

import ee.kaido.kmdb.controller.exception.BadRequestException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Checks {
    public static void checkIfStringNotEmpty(String text, String fieldName) {
        if (text.trim().isEmpty())
            throw new IllegalArgumentException(fieldName + " can't be empty!");
    }

    public static Date checkIsIsoDate(String date) throws BadRequestException {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            try {
                LocalDate localDate = LocalDate.parse(date, formatter);
                return Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
            } catch (DateTimeParseException e) {
                throw new BadRequestException(date + " is not a valid date format 'yyyy-MM-dd'");
            }
        }
        return null;
    }
}
