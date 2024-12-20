package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookStatus;
import BookmyBook.bmb.response.*;
import BookmyBook.bmb.response.dto.BookDetailAdmin_DTO;
import BookmyBook.bmb.response.dto.BookDetail_DTO;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.service.AdminService;
import BookmyBook.bmb.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminApiController {

    private final AdminService adminService;
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;

    //admin 도서 목록
    @GetMapping("/admin/books")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getAdminBooks(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletRequest request){

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String accessToken = authHeader.substring(7); //Bearer 제거

        AdminBookResponse adminBookResponse;

        //유효성 검사 및 기본값 설정
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        //도서 목록 조회
        adminBookResponse = adminService.getAdminBooks(page, size, category, keyword, accessToken);

        //page > total_pages
        if(adminBookResponse.getTotal_pages() < page){
            throw new ExceptionResponse(400, "요청한 페이지 번호가 전체 페이지 수를 초과", "INVALID_PAGE");
        }

        return ResponseEntity.ok(new ApiResponse(200, "도서 목록 및 대여 정보 조회 성공", adminBookResponse));
    }

    @PostMapping("/admin/books") // 도서 추가
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> insertBook(
            @ModelAttribute CreateBookRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) throws Exception{

        // Thumbnail 업로드 및 URL 반환
        String thumbnailUrl = s3Service.uploadFile(thumbnail);

        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor_name(request.getAuthor_name());
        book.setPublisher_name(request.getPublisher_name());
        book.setDescription(request.getDescription());
        book.setThumbnail(thumbnailUrl);
        book.setPublished_date(request.getPublished_date());
        book.setCreatedAt(LocalDateTime.now());
        book.setStatus(BookStatus.AVAILABLE);

        BookDetail_DTO bookDetail_dto = adminService.insert(book);

        return ResponseEntity.ok(new ApiResponse(201, "도서 등록 성공", bookDetail_dto));

    }

    @DeleteMapping("/admin/books/{isbn}") // ID로 한 권 선택 삭제
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> viewDelete(@PathVariable("isbn") String isbn) {

        if (isbn.length() != 13) {
            throw new ExceptionResponse(404, "해당 isbn 책 없음", "NOT_FOUNDED_ISBN");
        }

        // 도서 가져오기
        BookDetail_DTO bookDto = adminService.bring(isbn);

        // 도서의 이미지 URL 가져오기
        String thumbnailUrl = bookDto.getThumbnail();

        // 이미지 삭제 (S3에서)
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            s3Service.deleteFile(thumbnailUrl);
        }

        // 도서 삭제
        adminService.delete(bookDto.getIsbn());

        return ResponseEntity.ok(new ApiResponseNoResult(200, "도서 삭제 성공"));
    }

    // Admin 조회
    @GetMapping("/admin/books/{isbn}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> adminViewBook(@PathVariable("isbn") String isbn, HttpServletRequest request){

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(403, "접근 권한이 없습니다", "NO_PERMISSION");
        }

        BookDetailAdmin_DTO bookDto = adminService.bookView(isbn);

        return ResponseEntity.ok(new ApiResponse(200, "도서 상세 조회 성공", bookDto));
    }

    //회원목록&대여정보 조회
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getUsersLoanList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword){

        AdminUesrsResponse adminUesrsResponse = adminService.getUsersLoanList(page, size, category, keyword);

        //page > total_pages
        if(adminUesrsResponse.getTotal_pages() < page){
            throw new ExceptionResponse(400, "요청한 페이지 번호가 전체 페이지 수를 초과", "INVALID_PAGE");
        }

        return ResponseEntity.ok(new ApiResponse(200, "회원 목록 및 대여 정보 조회 성공", adminUesrsResponse));
    }

    @Data
    static class CreateBookRequest {
        private String isbn;
        private String title;
        private String author_name;
        private String publisher_name;
        private String description;
        private LocalDate published_date;
    }

}
