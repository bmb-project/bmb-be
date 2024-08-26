package BookmyBook.bmb.response;

import BookmyBook.bmb.response.dto.AdminBookDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AdminBookResponse {

    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private String category;
    private String keyword;
    private List<AdminBookDto> books;
}
