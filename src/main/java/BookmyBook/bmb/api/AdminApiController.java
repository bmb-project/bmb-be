package BookmyBook.bmb.api;

import BookmyBook.bmb.response.AdminBookResponse;
import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    //admin 도서 목록
    @PostMapping("/admin/books")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getAdminBooks(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletRequest request){

        //Cookie에서 Access Token 추출
        String accessToken = jwtUtil.getTokenFromCookies(request.getCookies(), "accessToken");

        AdminBookResponse adminBookResponse;
        try {
            //유효성 검사 및 기본값 설정
            if (page < 1) page = 1;
            if (size < 1) size = 10;

            //도서 목록 조회
            adminBookResponse = adminService.getAdminBooks(page, size, category, keyword, accessToken);

            return ResponseEntity.ok(new ApiResponse(200, "도서 목록 및 대여 정보 조회 성공", adminBookResponse));
        } catch (Exception e){
            throw new ExceptionResponse(404, "도서 목록 및 대여 정보 조회 실패", "FAIL_TO_LOAD_ADMIN");
        }
    }
}
