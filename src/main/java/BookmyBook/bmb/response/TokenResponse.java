package BookmyBook.bmb.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TokenResponse {

    private int ststusCode;
    private String message;

    public TokenResponse(int ststusCode, String message) {
        this.ststusCode = ststusCode;
        this.message = message;
    }
}
