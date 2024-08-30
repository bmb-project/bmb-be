package BookmyBook.bmb.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TokenResponse {

    private int statusCode;
    private String message;
    private String token;
    private Object result;

    public TokenResponse(int statusCode, String message, String token, Object result) {
        this.statusCode = statusCode;
        this.message = message;
        this.token = token;
        this.result = result;
    }
}
