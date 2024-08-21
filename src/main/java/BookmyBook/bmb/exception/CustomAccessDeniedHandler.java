package BookmyBook.bmb.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        // 상태 코드와 응답 형식 설정
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 형식의 에러 메시지를 작성
        String errorMessage = String.format("{\"status\":403, \"message\":\"접근 권한 없음\", \"error\":\"%s\"}",
                accessDeniedException.getMessage());

        response.getWriter().write(errorMessage);
    }

}
