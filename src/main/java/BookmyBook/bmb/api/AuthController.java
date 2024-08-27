package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.RefreshToken;
import BookmyBook.bmb.domain.UserRole;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.TokenResponse;
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

        if (storedRefreshToken.isPresent() && !storedRefreshToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            String userId = storedRefreshToken.get().getUserId();
            UserRole role = userService.getRoleById(userId);

            String newAccessToken = jwtUtil.createAccessToken(userId, role); // UserRole.USER는 예시입니다
            String newRefreshToken = jwtUtil.createRefreshToken(userId);

            // 기존 Refresh Token 삭제
            refreshTokenService.deleteRefreshToken(refreshToken);

            // 새로운 Refresh Token 저장
            refreshTokenService.saveRefreshToken(userId, newRefreshToken, LocalDateTime.now().plusDays(7));

            // 액세스 토큰을 쿠키에 저장
            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true); // 클라이언트 측 스크립트에서 접근 불가
            accessTokenCookie.setPath("/"); // 모든 경로에서 접근 가능
            accessTokenCookie.setMaxAge(3600); // 1시간 유효

            // 쿠키로 새로운 Refresh Token 설정
            Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new TokenResponse(200, "token 갱신 성공"));
        } else {
            return ResponseEntity.status(401).body(new ExceptionResponse(401, "유효하지 않은 Refresh Token", "UNDEFINED_REFRESH_TOKEN"));
        }
    }
}

