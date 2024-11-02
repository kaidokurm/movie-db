package ee.kaido.kmdb.exception;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;


@ControllerAdvice//adds to all controllers
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(IllegalArgumentException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(ElementExistsException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(PropertyValueException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(IllegalStateException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(EntityNotFoundException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(ResourceNotFoundException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(HttpMessageNotReadableException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(BadRequestException e) {
        return getBadRequestExceptionResponseModelResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private static ResponseEntity<ExceptionResponseModel> getBadRequestExceptionResponseModelResponseEntity(HttpStatus badRequest, String message) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(badRequest);
        response.setHttpStatusCode(badRequest.value());
        response.setTimestamp(new Date());
        response.setMessage(message);
        return ResponseEntity.status(badRequest).body(response);
    }

    //for validation errors
    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(MethodArgumentNotValidException e) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        response.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(new Date());

        StringBuilder messageBuilder = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(error -> {
            messageBuilder.append(error.getDefaultMessage()).append("; "); // Custom message
        });
        response.setMessage(messageBuilder.toString().trim());
        return ResponseEntity.badRequest().body(response);
    }
}
