package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    //DTO 방식
    @PostMapping("/user/signup")
    @ResponseBody
    public ResponseEntity<?> singupUser(@RequestBody @Valid CreateUserRequest request){

        User user = new User();
        user.setNickname(request.getNickname());
        user.setUser_id(request.user_id);
        user.setPassword(request.password);
        user.getCreated_at();
        user.setRole(UserRole.USER);

        User user1 = userService.join(user);

        return ResponseEntity.ok(new ApiResponse(200, "회원가입 성공", user1));

    }

    @Data
    static class CreateUserRequest {
        private String nickname;
        private String user_id;
        private String password;
    }

    /*@Data
    static class CreateUserResponse {
        private Long id;
        private String user_id;
        private String nickname;
        private LocalDateTime create_at;

        public CreateUserResponse(User user) {
            this.id = user.getId();
            this.user_id = user.getUser_id();
            this.nickname = user.getNickname();
            this.create_at = user.getCreated_at();
        }
    }*/
}
