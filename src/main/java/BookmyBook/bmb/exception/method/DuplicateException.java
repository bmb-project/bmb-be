package BookmyBook.bmb.exception.method;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException{
    private final String code;

    public DuplicateException(String message, String code){
        super(message);
        this.code = code;
    }

}
