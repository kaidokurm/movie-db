package ee.kaido.kmdb.service.checkers;

public class Checks {
    public static String checkIfStringNotEmpty(String text, String fieldName) {
        if (text.trim().isEmpty())
            throw new IllegalArgumentException(fieldName + " can't be empty!");
        return text;
    }
//
//    public static Date checkIsIsoDate(String date) throws BadRequestException {
//        if (date != null) {
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
//            try {
//                LocalDate localDate = LocalDate.parse(date, formatter);
//                return Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
//            } catch (DateTimeParseException e) {
//                throw new BadRequestException(date + " is not a valid date format 'yyyy-MM-dd'");
//            }
//        }
//        return null;
//    }
}
