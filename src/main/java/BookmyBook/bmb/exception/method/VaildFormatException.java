package BookmyBook.bmb.exception.method;

import lombok.Getter;

@Getter
public class VaildFormatException extends RuntimeException{
    private final String code;

    public VaildFormatException(String message, String code){
        super(message);
        this.code = code;
    }
}
