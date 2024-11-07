package BookmyBook.bmb.response.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AdminUsersDto {
    private String user_id;
    private String nickname;
    private List<AdminUserLoanDto> loans;

    public AdminUsersDto(String user_id, String nickname, List<AdminUserLoanDto> loans) {
        this.user_id = user_id;
        this.nickname = nickname;
        this.loans = loans;
    }
}
