package BookmyBook.bmb.security;

import BookmyBook.bmb.domain.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException{
        // 공개된 엔드포인트를 체크하여 인증을 요구하지 않는 경우
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response); // 필터 체인을 계속 진행
            return;
        }

        // 쿠키에서 토큰 추출
        String accessToken = jwtUtil.getTokenFromCookies(request.getCookies(), "accessToken");

        if (accessToken != null) {
            try {
                String user_id = jwtUtil.getUserId(accessToken, "access");
                String roleString = jwtUtil.getRole(accessToken);
                UserRole role = UserRole.valueOf(roleString);

                if (user_id != null && jwtUtil.validateToken(accessToken, user_id, "access")) {
                    //역할에 따른 권한 설정
                    Collection<? extends GrantedAuthority> authorities = getAuthorities(role);

                    //사용자 아이디으로 인증 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user_id, null, authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 유효하지 않은 토큰인 경우
                    setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "INVALID_TOKEN", "존재하지 않는 token");
                    return;
                }
            } catch (Exception e){
                // 예외를 로그로 기록하고 403 오류를 반환합니다
                System.out.println(e.getMessage());
                setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "AUTHENTICATION_ERROR", "권한 없음");
                return;
            }
        }else {
            // 액세스 토큰이 없을 경우 처리
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "NO_TOKEN", "token 없음");
            return;
        }
        filterChain.doFilter(request, response); // 필터 체인을 계속 진행
    }

    private boolean isPublicEndpoint(String requestUri) {
        // 공개된 엔드포인트를 정의
        return requestUri.startsWith("/user/signin") || requestUri.startsWith("/user/signup");
    }

    public Collection<? extends GrantedAuthority> getAuthorities(UserRole role){
        //역할에 따른 권한 설정
        if(role == UserRole.ADMIN){
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }else if(role == UserRole.USER){
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Collections.emptyList();
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
