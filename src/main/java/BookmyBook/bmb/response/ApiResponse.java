package BookmyBook.bmb.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiResponse {

    private int statusCode;
    private String message;
    private Object result;

    public ApiResponse(int statusCode, String message, Object result) {
        this.statusCode = statusCode;
        this.message = message;
        this.result = result;
    }

    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
