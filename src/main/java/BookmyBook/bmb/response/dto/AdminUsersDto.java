package BookmyBook.bmb.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class AdminUsersDto {
    private String user_id;
    private String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime created_at;

    private List<AdminUserLoanDto> loans;

    public AdminUsersDto(String user_id, String nickname, LocalDateTime created_at, List<AdminUserLoanDto> loans) {
        this.user_id = user_id;
        this.nickname = nickname;
        this.created_at = created_at;
        this.loans = loans;
    }
}
