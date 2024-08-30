package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.response.*;
import BookmyBook.bmb.response.dto.UserDto;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.service.RefreshTokenService;
import BookmyBook.bmb.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

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
    public ResponseEntity<?> signinUser(@RequestBody @Valid CreateUserRequest request, HttpServletResponse response){
        User user = new User();
        user.setUser_id(request.getUser_id());
        user.setPassword(request.getPassword());

        User login = userService.login(user);
        UserDto userDto = new UserDto(login.getUser_id(), login.getNickname(), login.getRole());

        // Access Token과 Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(login.getUser_id(), login.getRole());
        String refreshToken = jwtUtil.createRefreshToken(login.getUser_id());

        // 기존 토큰 삭제
        refreshTokenService.deleteByUserId(user.getUser_id());

        // Refresh Token을 쿠키에 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        refreshTokenCookie.setSecure(true); // HTTPS를 사용할 때만 전송
        refreshTokenCookie.setPath("/"); // 쿠키의 유효 범위 설정

        //Cookie를 response header에 저장
        response.addCookie(refreshTokenCookie);

        // 데이터베이스에 Refresh Token 저장
        refreshTokenService.saveRefreshToken(user.getUser_id(), refreshToken, LocalDateTime.now());

        return ResponseEntity.ok(new TokenResponse(200, "로그인 성공", accessToken, userDto));
    }

    //회원별 대여 목록 조회
    @GetMapping("/user/loan")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> getUserLoan(
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
        String accessToken = authHeader.substring(7); //Bearer 제거

        UserLoanResponse userLoanResponse;

        //유효성 검사 및 기본값 설정
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        //도서 대여 목록 조회
        userLoanResponse = userService.getUserLoan(page, size, category, keyword, accessToken);

        return ResponseEntity.ok(new ApiResponse(200, "대여 목록 조회 성공", userLoanResponse));
    }

    //회원별 좋아요 목록 조회
    @GetMapping("/user/wish")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> getUserWish(
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
        String accessToken = authHeader.substring(7); //Bearer 제거

        UserWishResponse userWishResponse;

        //유효성 검사 및 기본값 설정
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        //도서 목록 조회
        userWishResponse = userService.getUserWish(page, size, category, keyword, accessToken);

        return ResponseEntity.ok(new ApiResponse(200, "좋아요 목록 조회 성공", userWishResponse));
    }


    @Data
    static class CreateUserRequest {
        private String nickname;
        private String user_id;
        private String password;
    }
}
