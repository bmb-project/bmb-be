package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class BookDetailAdmin_DTO {
    private String isbn;
    private String title;
    private String description;
    private String thumbnail;
    private String author_name;
    private String publisher_name;
    private LocalDate published_date;
    private BookStatus status;
    private List<AdminLoanDto> loans;

    public BookDetailAdmin_DTO(String isbn, String title, String description, String thumbnail, String author_name, String publisher_name,
                               LocalDate published_date, BookStatus status, List<AdminLoanDto> loans) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.published_date = published_date;
        this.status = status;
        this.loans = loans;
    }
}
