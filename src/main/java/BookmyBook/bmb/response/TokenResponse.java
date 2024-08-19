package BookmyBook.bmb.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TokenResponse {

    private int ststusCode;
    private String message;
    private Object result;
    private String token;

    public TokenResponse(int ststusCode, String message, String token, Object result) {
        this.ststusCode = ststusCode;
        this.message = message;
        this.result = result;
        this.token = token;
    }
}
