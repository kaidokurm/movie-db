package ee.kaido.kmdb.controller.exception;

public class ResourceNotFoundException extends Throwable {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
