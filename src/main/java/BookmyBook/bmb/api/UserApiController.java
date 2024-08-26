package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.response.*;
import BookmyBook.bmb.response.dto.UserDto;
import BookmyBook.bmb.security.JwtUtil;
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

    //회원가입 - 참고
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
        UserDto userDto = new UserDto(join.getUser_id(), join.getNickname(), join.getRole());

        return ResponseEntity.ok(new ApiResponse(200, "회원가입 성공", userDto));

    }

    //로그인
    @PostMapping("/user/signin")
    @ResponseBody
    public ResponseEntity<?> signinUser(@RequestBody @Valid CreateUserRequest request){
        User user = new User();
        user.setUser_id(request.getUser_id());
        user.setPassword(request.getPassword());

        User login = userService.login(user);
        UserDto userDto = new UserDto(login.getUser_id(), login.getNickname(), login.getRole());

        String token = jwtUtil.createToken(login.getUser_id(), login.getRole());

        return ResponseEntity.ok(new TokenResponse(200, "로그인 성공", token, userDto));
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

        UserDto userDto = new UserDto(user.getUser_id(), user.getNickname(), user.getRole());

        return ResponseEntity.ok(new ApiResponse(200, "회원 조회 성공", userDto));
    }

    //회원별 대여 목록 조회
    @GetMapping("/user/{id}/loan")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> getUserLoan(
            @PathVariable("id") String user_id,
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword){

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String token = authHeader.substring(7); //Bearer 제거

        UserLoanResponse userLoanResponse;


        //유효성 검사 및 기본값 설정
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        //도서 목록 조회
        userLoanResponse = userService.getUserLoan(page, size, category, keyword, token, user_id);

        return ResponseEntity.ok(new ApiResponse(200, "대여 목록 조회 성공", userLoanResponse));
    }


    @Data
    static class CreateUserRequest {
        private String nickname;
        private String user_id;
        private String password;
    }
}
