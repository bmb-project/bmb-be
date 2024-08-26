package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.BookStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AdminBookDto {
    private String isbn;
    private String title;
    private String thumbnail;
    private String author_name;
    private String publisher_name;
    private BookStatus status;
    private Long wish_count;
    private boolean wished;
    private List<AdminLoanDto> loans;

    public AdminBookDto(String isbn, String title, String thumbnail, String author_name, String publisher_name,
                        BookStatus status, Long wish_count, boolean wished, List<AdminLoanDto> loans) {
        this.isbn = isbn;
        this.title = title;
        this.thumbnail = thumbnail;
        this.author_name = author_name;
        this.publisher_name = publisher_name;
        this.status = status;
        this.wish_count = wish_count;
        this.wished = wished;
        this.loans = loans;
    }
}
