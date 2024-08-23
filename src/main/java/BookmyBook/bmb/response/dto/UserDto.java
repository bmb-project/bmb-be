package BookmyBook.bmb.response.dto;

import BookmyBook.bmb.domain.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {

    private String user_id;
    private String nickname;
    private UserRole userRole;

    public UserDto (String user_id, String nickname, UserRole userRole) {
        this.user_id = user_id;
        this.nickname = nickname;
        this.userRole = userRole;
    }
}
