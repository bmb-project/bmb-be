package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.dto.UserDto;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.response.TokenResponse;
import BookmyBook.bmb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    //회원가입
    @PostMapping("/user/signup")
    @ResponseBody
    public ResponseEntity<?> singupUser(@RequestBody @Valid CreateUserRequest request){

        User user = new User();
        user.setNickname(request.getNickname());
        user.setUser_id(request.user_id);
        user.setPassword(request.password);
        user.getCreated_at();
        user.setRole(UserRole.USER);

        User join = userService.join(user);

        return ResponseEntity.ok(new ApiResponse(200, "회원가입 성공", join));

    }

    //로그인
    @PostMapping("/user/signin")
    @ResponseBody
    public ResponseEntity<?> signinUser(@RequestBody @Valid CreateUserRequest request){
        User user = new User();
        user.setUser_id(request.getUser_id());
        user.setPassword(request.getPassword());

        User login = userService.login(user);

        String token = jwtUtil.createToken(login.getUser_id(), login.getNickname());

        return ResponseEntity.ok(new TokenResponse(200, "로그인 성공", token, login));
    }

    //회원 정보 조회
    @GetMapping("/user")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> getUserList(HttpServletRequest request){
        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String token = authHeader.substring(7); //Bearer 제거

        //JWT token에서 사용자 정보 조회
        User user = userService.findOne(token);

        UserDto userDto = new UserDto(user.getId(), user.getUser_id(), user.getNickname(), user.getRole());

        return ResponseEntity.ok(new ApiResponse(200, "회원 조회 성공", userDto));
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
