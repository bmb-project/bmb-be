package BookmyBook.bmb.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AdminUserLoanDto {
    private String isbn;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime loan_at;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime return_at;

    public AdminUserLoanDto(String isbn, String title, LocalDateTime loan_at, LocalDateTime return_at) {
        this.isbn = isbn;
        this.title = title;
        this.loan_at = loan_at;
        this.return_at = return_at;
    }
}
