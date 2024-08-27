package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserWishDto {

    private String isbn;
    private String title;
    private String thumbnail;
    private String author_name;
    private String publisher_name;
    private BookStatus status;

    public UserWishDto(String isbn, String title, String thumbnail, String author_name, String publisher_name, BookStatus status) {
        this.isbn = isbn;
        this.title = title;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.status = status;
    }
}
