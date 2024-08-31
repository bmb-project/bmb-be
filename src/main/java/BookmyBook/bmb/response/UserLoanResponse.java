package BookmyBook.bmb.response;

import BookmyBook.bmb.response.dto.UserLoanDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserLoanResponse {

    private int total_pages;
    private int current_page;
    private int page_size;
    private long total_items;
    private String category;
    private String keyword;
    private List<UserLoanDto> books;
}
