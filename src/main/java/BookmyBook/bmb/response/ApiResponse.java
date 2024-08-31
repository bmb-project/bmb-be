package BookmyBook.bmb.response;

import BookmyBook.bmb.domain.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiResponse {

    private int ststusCode;
    private String message;
    private Object result;

    public ApiResponse(int ststusCode, String message, Object result) {
        this.ststusCode = ststusCode;
        this.message = message;
        this.result = result;
    }

    public ApiResponse(int ststusCode, String message) {
        this.ststusCode = ststusCode;
        this.message = message;
    }
}
