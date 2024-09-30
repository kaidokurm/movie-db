package ee.kaido.kmdb.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;


@ControllerAdvice//adds to all controllers
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(IllegalArgumentException e) {
        return getExceptionResponseModelResponseEntity(e.getMessage());
    }

    private ResponseEntity<ExceptionResponseModel> getExceptionResponseModelResponseEntity(String message) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.NOT_ACCEPTABLE);
        response.setHttpStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        response.setTimestamp(new Date());
        response.setMessage(message);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(ResourceNotFoundException e) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setHttpStatusCode(HttpStatus.NOT_FOUND.value());
        response.setTimestamp(new Date());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(HttpMessageNotReadableException e) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        response.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(new Date());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(BadRequestException e) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        response.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(new Date());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(ElementExistsException e) {
        return getExceptionResponseModelResponseEntity(e.getMessage());
    }
}
