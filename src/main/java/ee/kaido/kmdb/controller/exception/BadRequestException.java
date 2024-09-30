package ee.kaido.kmdb.controller.exception;

public class BadRequestException extends Throwable {
    public BadRequestException(String message) {
        super(message);
    }
}
