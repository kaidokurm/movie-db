package ee.kaido.kmdb.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
public class ExceptionResponseModel {
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private Date timestamp;
    private String message;
}
