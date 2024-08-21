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
import java.io.PrintWriter;
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
                String user_id = jwtUtil.getUserId(token);

                if (user_id != null && jwtUtil.validateToken(token, user_id)) {
                    //사용자 아이디으로 인증 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user_id, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 유효하지 않은 토큰인 경우
                    setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "INVALID_TOKEN", "존재하지 않는 TOKEN");
                    return;
                }
            } catch (Exception e){
                // 예외를 로그로 기록하고 403 오류를 반환합니다
                setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "AUTHENTICATION_ERROR", "권한 없음");
                return;
            }
        }
        filterChain.doFilter(request, response); // 필터 체인을 계속 진행
    }

    private void setErrorResponse(HttpServletResponse response, int statusCode, String code, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.write("{\"status\":\""+statusCode+"\"\n\t\"error\": \"" + code + "\"\n\t\"message\":\""+message+"\"}");
        writer.flush();
    }
}
