package ee.kaido.kmdb.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.NoSuchElementException;


@ControllerAdvice//adds to all controllers
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(NoSuchElementException e) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setHttpStatusCode(HttpStatus.NOT_FOUND.value());
        response.setTimestamp(new Date());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseModel> handleException(ElementExistsException e) {
        ExceptionResponseModel response = new ExceptionResponseModel();
        response.setHttpStatus(HttpStatus.NOT_ACCEPTABLE);
        response.setHttpStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        response.setTimestamp(new Date());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
}
