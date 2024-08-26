package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class UserLoanDto {

    private String isbn;
    private String title;
    private String thumbnail;
    private String author_name;
    private String publisher_name;
    private BookStatus status;

    //UserLoanList
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime loan_at;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime return_at;

    public UserLoanDto(String isbn, String title, String thumbnail, String author_name, String publisher_name, BookStatus status,
                   LocalDateTime loan_at, LocalDateTime return_at) {
        this.isbn = isbn;
        this.title = title;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.status = status;
        this.loan_at = loan_at;
        this.return_at = return_at;
    }
}
