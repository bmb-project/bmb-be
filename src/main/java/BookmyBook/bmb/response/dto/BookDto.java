package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


// 도서 목록 : 조히할 따랑은 다름

// 도서 목록과 도서 추가를 하나로 통일
@Getter @Setter
public class BookDto {

    private String isbn;
    private String title;
    private String thumbnail;
    private String author_name;
    private String publisher_name;
    private BookStatus status;
    private Long wish_count;
    private boolean wished;

    //선우`s dto
    private long id;
    private String description;
    private LocalDateTime created_at;
    private LocalDate published_date;

    public BookDto(String isbn, String title, String thumbnail, String author_name, String publisher_name, BookStatus status,
                   Long wish_count, boolean wished) {

        this.isbn = isbn;
        this.title = title;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.status = status;
        this.wish_count = wish_count;
        this.wished = wished;
    }

    // 선우`s 생성자 - 도서 추가
    public BookDto(String isbn, long id, String title, String thumbnail, String author_name,
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
