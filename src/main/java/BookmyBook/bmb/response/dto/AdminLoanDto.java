package BookmyBook.bmb.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AdminLoanDto {

    private Long id;
    private String nickname;
    private String user_id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime loan_at;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime return_at;

    public AdminLoanDto(Long id, String nickname, String user_id, LocalDateTime loan_at, LocalDateTime return_at) {
        this.id = id;
        this.nickname = nickname;
        this.user_id = user_id;
        this.loan_at = loan_at;
        this.return_at = return_at;
    }
}
