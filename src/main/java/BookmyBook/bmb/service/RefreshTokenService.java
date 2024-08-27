package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.RefreshToken;
import BookmyBook.bmb.repository.RefreshTokenRepository;
import BookmyBook.bmb.response.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // Refresh Token 저장
    @Transactional
    public void saveRefreshToken(String userId, String token, LocalDateTime expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepository.save(refreshToken);
    }

    // Refresh Token 조회
    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // Refresh Token 삭제
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    // 재 로그인 시 Refresh Token 삭제
    @Transactional
    public void deleteByUserId(String userId){
        // userId가 null 또는 빈 값일 경우 예외를 던지기
        if (userId == null || userId.isEmpty()) {
            throw new ExceptionResponse(404, "user_id의 값이 필요", "UNDEFINED_USER_ID");
        }

        // 해당 userId에 대한 Refresh token이 존재하는지 확인
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(userId);
        if (tokenOptional.isPresent()) {
            // 존재할 경우 삭제
            refreshTokenRepository.deleteByUserId(userId);
        }
    }
}