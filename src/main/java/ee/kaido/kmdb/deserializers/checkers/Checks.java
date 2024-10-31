package ee.kaido.kmdb.deserializers.checkers;

import ee.kaido.kmdb.exception.BadRequestException;

public class Checks {
    public static String checkIfStringNotEmpty(String text, String fieldName)
            throws BadRequestException {
        if (text.trim().isEmpty())
            throw new BadRequestException(fieldName + " can't be empty!");
        return text;
    }

    public static String wordFirstLetterToUpper(String name) {
        name = name.trim().replaceAll("\\s+", " ");
        if (!name.isEmpty()) {
            String[] words = name.split(" ");
            StringBuilder normalizedName = new StringBuilder();
            for (String word : words) {
                if (!normalizedName.isEmpty()) normalizedName.append(" ");
                normalizedName.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
            return normalizedName.toString();
        }
        throw new IllegalArgumentException("Name cannot be empty!");
    }
}
