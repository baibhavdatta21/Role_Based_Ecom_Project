package api.gateway.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex){

        ErrorResponse errorResponse =
                new ErrorResponse(ex.getMessage(),ex.getStatus());

        return new ResponseEntity<>(errorResponse,ex.getStatus());
    }
}
