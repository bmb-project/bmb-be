package BookmyBook.bmb.response;

import BookmyBook.bmb.response.dto.BookDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BookResponse {

    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private String category;
    private String keyword;
    private List<BookDto> books;
}
