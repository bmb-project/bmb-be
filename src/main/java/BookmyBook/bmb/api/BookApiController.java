package BookmyBook.bmb.api;

import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.BookResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;

    //도서 목록 조회
    @GetMapping("/books")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> getBookList(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String token = authHeader.substring(7); //Bearer 제거

        BookResponse bookResponse;
        try {
            //유효성 검사 및 기본값 설정
            if (page < 1) page = 1;
            if (size < 1) size = 10;

            //도서 목록 조회
            bookResponse = bookService.getBooks(page, size, category, keyword, token);

            return ResponseEntity.ok(new ApiResponse(200, "도서 목록 조회 성공", bookResponse));
        } catch (Exception e){
            throw new ExceptionResponse(404, "도서 목록 조회 실패", "FAIL_TO_LOAD");
        }
    }
}
