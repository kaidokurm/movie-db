package ee.kaido.kmdb.service.checkers;

public class Checks {
    public static String checkIfStringNotEmpty(String text, String fieldName) {
        if (text.trim().isEmpty())
            throw new IllegalArgumentException(fieldName + " can't be empty!");
        return text;
    }
}
