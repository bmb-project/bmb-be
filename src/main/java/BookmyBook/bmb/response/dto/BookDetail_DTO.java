package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookDetail_DTO {

    private String isbn;
    private long id;
    private String title;
    private String author_name;
    private String publisher_name;
    private String thumbnail;
    private BookStatus status;
    private String description;
    private LocalDateTime created_at;
    private LocalDate published_date;

    // 선우`s 생성자 - 도서 추가
    public BookDetail_DTO(String isbn, long id, String title, String thumbnail, String author_name,
                          String publisher_name, BookStatus status, String description,
                          LocalDate published_date, LocalDateTime created_at) {
        this.isbn = isbn;
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.status = status;
        this.description = description;
        this.published_date = published_date;
        this.created_at = created_at;
    }

}
