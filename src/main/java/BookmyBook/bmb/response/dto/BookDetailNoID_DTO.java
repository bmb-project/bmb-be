package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookDetailNoID_DTO {

    private String isbn;
    private String title;
    private String description;
    private String thumbnail;
    private String author_name;
    private String publisher_name;
    private LocalDate published_date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime created_at;

    private BookStatus status;


    public BookDetailNoID_DTO(String isbn, String title, String description, String thumbnail,
                          String author_name, String publisher_name, LocalDate published_date,
                          LocalDateTime created_at, BookStatus status) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.published_date = published_date;
        this.created_at = created_at;
        this.status = status;
    }

}
