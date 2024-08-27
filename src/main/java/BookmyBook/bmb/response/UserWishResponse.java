package BookmyBook.bmb.response;

import BookmyBook.bmb.response.dto.UserWishDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserWishResponse {

    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private String category;
    private String keyword;
    private List<UserWishDto> books;
}
