package BookmyBook.bmb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException{
        // Authorization 헤더에서 토큰 추출
        String token = request.getHeader("Authorization");

        // 토큰이 존재하고 "Bearer "로 시작하는 경우
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer "를 제거한 실제 토큰
            try {
                String username = jwtUtil.getUsername(token);

                if (username != null && jwtUtil.validateToken(token, username)) {
                    //사용자 이름으로 인증 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e){
                // 예외를 로그로 기록하고 401 오류를 반환합니다
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }
        filterChain.doFilter(request, response); // 필터 체인을 계속 진행
    }
}
