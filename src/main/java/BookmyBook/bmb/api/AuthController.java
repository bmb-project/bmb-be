package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.RefreshToken;
import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.TokenResponse;
import BookmyBook.bmb.response.dto.UserDto;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.repository.RefreshTokenRepository;
import BookmyBook.bmb.service.RefreshTokenService;
import BookmyBook.bmb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // Refresh Token으로 Access Token 재발급
    @PostMapping("/auth")
    public ResponseEntity<?> refreshTokens(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken,
                                           HttpServletResponse response) {
        Optional<RefreshToken> storedRefreshToken = refreshTokenService.getRefreshToken(refreshToken);

        if (storedRefreshToken.isPresent()) {
            String userId = storedRefreshToken.get().getUserId();
            UserRole role = userService.getRoleById(userId);

            String newAccessToken = jwtUtil.createAccessToken(userId, role);
            String newRefreshToken = jwtUtil.createRefreshToken(userId);

            UserDto userInfo = userService.getUserInfo(userId);

            // 기존 Refresh Token 삭제
            refreshTokenService.deleteRefreshToken(refreshToken);

            // 새로운 Refresh Token 저장
            refreshTokenService.saveRefreshToken(userId, newRefreshToken, LocalDateTime.now());

            // 쿠키로 새로운 Refresh Token 설정
            Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");

            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new TokenResponse(200, "token 갱신 성공", newAccessToken, userInfo));
        } else {
            throw new ExceptionResponse(401, "유효하지 않은 Refresh Token", "UNDEFINED_REFRESH_TOKEN");
        }
    }
}

