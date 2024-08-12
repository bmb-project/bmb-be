package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    //Entity 1:1방식
    @PostMapping("/api/v1/members")
    @ResponseBody
    public CreateUserResponse saveUserV1(@RequestBody @Valid User user)
    {
        Long id = userService.join(user);
        return new CreateUserResponse(id);
    }

    //DTO 방식
    @PostMapping("/api/v2/members")
    @ResponseBody
    public CreateUserResponse saveUserV2(@RequestBody @Valid UserApiController.CreateUserRequest request){

        User user = new User();
        user.setNickname(request.getNickname());
        user.setUser_id(request.user_id);

        Long id = userService.join(user);
        return new CreateUserResponse(id);
    }

    @Data
    static class CreateUserRequest {
        private String nickname;
        private String user_id;
    }

    @Data
    static class CreateUserResponse {
        private Long id;
        public CreateUserResponse(Long id) {
            this.id = id;
        }
    }
}
