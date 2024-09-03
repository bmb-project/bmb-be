package BookmyBook.bmb.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseNoResult {

    private int statusCode;
    private String message;

    public ApiResponseNoResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
