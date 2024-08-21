package BookmyBook.bmb.exception;

import BookmyBook.bmb.response.ErrorResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //BAD_REQUEST
    @ExceptionHandler(ExceptionResponse.class)
    public ResponseEntity<ErrorResponse> handleunexistException(ExceptionResponse ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ex.getStatus())
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus())).body(errorResponse);
    }

    //INTERNAL_SERVER_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(500)
                .code("SERVER_ERROR")
                .message("서버 오류")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

