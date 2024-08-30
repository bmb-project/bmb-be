package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookStatus;
import BookmyBook.bmb.response.AdminBookResponse;
import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.dto.BookDetail_DTO;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminApiController {

    private final AdminService adminService;
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

    @PostMapping("admin/books") // 도서 추가
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> insertBook(@RequestBody CreateBookRequest request){
        try {
            Book book = new Book();
            book.setIsbn(request.isbn);
            book.setTitle(request.title);
            book.setAuthor_name(request.author_name);
            book.setPublisher_name(request.publisher_name);
            book.setThumbnail(request.thumbnail);
            book.setDescription(request.description);
            book.setPublished_date(request.published_date);
            book.setCreated_at(LocalDateTime.now());
            log.info("asdf : {}", request.available);

            if(request.available){
                book.setStatus(BookStatus.AVAILABLE);
            }else {
                throw new ExceptionResponse(400, "도서 등록 실패", "UNDEFIND");
            }

            log.info("Request: {}", request);
            log.info("CreatedTime : {}", book.getCreated_at());
            Book insert = adminService.insert(book);
            BookDetail_DTO bookDto = new BookDetail_DTO(book.getIsbn(), book.getId(), book.getTitle(), book.getThumbnail(), book.getAuthor_name(),
                    book.getPublisher_name(), book.getStatus(), book.getDescription(),
                    book.getPublished_date(), book.getCreated_at());

            return ResponseEntity.ok(new ApiResponse(200, "도서 추가 성공", bookDto));
        }catch (Exception e){
            throw new ExceptionResponse(409, "도서 추가 실패 - ISBN 중복", "ALREADY_INSERT_BOOK_ADMIN");
        }


    }

    @DeleteMapping("/books") // ID로 한 권 선택 삭제
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> viewDelete(@RequestBody CreateBookRequest request){
        log.info("qwer");
        String isbn = request.getIsbn();
        log.info("/books Start : {}", isbn);

        // 도서 가져오기
        BookDetail_DTO bookDto = adminService.bring(isbn);

        // 도서가 존재하지 않을 경우 처리
        if (bookDto == null) {
            log.info("도서 ID {}에 해당하는 도서를 찾을 수 없습니다.", isbn);
            throw new ExceptionResponse(404, "도서 목록 및 정보 조회 실패", "FAIL_TO_LOAD_ADMIN");
        }

        log.info("ID : {}", bookDto.getId());
        log.info("ISBN : {}", bookDto.getIsbn());
        log.info("Title : {}", bookDto.getTitle());
        log.info("Author name : {}", bookDto.getAuthor_name());
        log.info("Publisher name : {}", bookDto.getPublisher_name());
        log.info("Thumbnail : {}", bookDto.getThumbnail());
        log.info("Description : {}", bookDto.getDescription());
        log.info("Published_date : {}", bookDto.getPublished_date());
        log.info("Created at : {}", bookDto.getCreated_at());
        log.info("Status : {}", bookDto.getStatus());

        log.info("Delete Start");
        adminService.delete(bookDto.getId());

        return ResponseEntity.ok(new ApiResponse(200, "도서 삭제 성공", bookDto));
    }

    @Data
    static class CreateBookRequest {
        private String isbn;
        private long id;
        private String title;
        private String author_name;
        private String publisher_name;
        private String thumbnail;
        private String description;
        private LocalDate published_date;
        private LocalDateTime created_at;
        private boolean available;
    }

}
