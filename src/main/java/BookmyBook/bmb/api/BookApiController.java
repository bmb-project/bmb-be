package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookStatus;
import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.BookResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.dto.BookDetail_DTO;
import BookmyBook.bmb.response.dto.WishDto;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookApiController {

    private final BookService bookService;
    private final JwtUtil jwtUtil;

    //도서 목록 조회
    @GetMapping("/books")
    @PreAuthorize("hasRole('User') or hasRole('Admin')") // 참고
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
        String accessToken = authHeader.substring(7); //Bearer 제거

        BookResponse bookResponse;
        try {
            //유효성 검사 및 기본값 설정
            if (page < 1) page = 1;
            if (size < 1) size = 10;

            //도서 목록 조회
            bookResponse = bookService.getBooks(page, size, category, keyword, accessToken);

            return ResponseEntity.ok(new ApiResponse(200, "도서 목록 조회 성공", bookResponse));
        } catch (Exception e){
            throw new ExceptionResponse(404, "도서 목록 조회 실패", "FAIL_TO_LOAD");
        }
    }

    //도서별 좋아요 목록 조회
    @GetMapping("/books/{isbn}/wish")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> getUserWish(@PathVariable("isbn") String isbn, HttpServletRequest request){

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String accessToken = authHeader.substring(7); //Bearer 제거

        //도서 목록 조회
        WishDto wishDto = bookService.getBookWish(accessToken, isbn);

        return ResponseEntity.ok(new ApiResponse(200, "좋아요 목록 조회 성공", wishDto));
    }

    //도서 좋아요 하기
    @PostMapping("/books/{isbn}/wish")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> booksWish(@PathVariable("isbn") String isbn, HttpServletRequest request){

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String accessToken = authHeader.substring(7); //Bearer 제거

        //좋아요 등록
        WishDto wishDto = bookService.saveWish(isbn, accessToken);

        return ResponseEntity.ok(new ApiResponse(201, "좋아요 성공", wishDto));
    }

    @DeleteMapping("/books/{isbn}/wish")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> deleteWish(@PathVariable("isbn") String isbn, HttpServletRequest request){

        //Authorization header에서 token 추출
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionResponse(401, "존재하지 않는 TOKEN", "INVALID_TOKEN");
        }
        String accessToken = authHeader.substring(7); //Bearer 제거

        //좋아요 취소
        WishDto wishDto = bookService.deleteWish(isbn, accessToken);

        return ResponseEntity.ok(new ApiResponse(200, "좋아요 취소 성공", wishDto));
    }

    @PostMapping("/books/insert") // 도서 추가
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> insertBook(@RequestBody CreateBookRequest request){
        Book book = new Book();
        book.setId(request.id);
        book.setIsbn(request.isbn);
        book.setTitle(request.title);
        book.setAuthor_name(request.author_name);
        book.setPublisher_name(request.publisher_name);
        book.setThumbnail(request.thumbnail);
        book.setDescription(request.description);
        book.setPublished_date(request.published_date);
        book.setCreated_at(LocalDateTime.now());
        book.setStatus(request.status);

        log.info("Request: {}", request);
        log.info("CreatedTime : {}", book.getCreated_at());
        Book insert = bookService.insert(book);
        BookDetail_DTO bookDto = new BookDetail_DTO(book.getIsbn(), book.getId(), book.getTitle(), book.getThumbnail(), book.getAuthor_name(),
                book.getPublisher_name(), book.getStatus(), book.getDescription(),
                book.getPublished_date(), book.getCreated_at());

        return ResponseEntity.ok(new ApiResponse(200, "도서 추가 성공", bookDto));
    }


    // 도서 상세 조회
    @GetMapping("/books/{isbn}")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> viewBook(@PathVariable("isbn") String isbn) {
        log.info(isbn);
        log.info("/books/??? Start : " + isbn);
        BookDetail_DTO bookDto = null;

        // 도서 존재 확인
        boolean tf = bookService.bookKakuninn(isbn);

        // 확인 후 도서 가져오기
        if(tf){
            bookDto = bookService.bookView(isbn);
        }else {
            // 도서가 존재하지 않을 경우 처리
            log.info("도서 ISBN : {}에 해당하는 도서를 찾을 수 없습니다.", isbn);
            throw new ExceptionResponse(404, "해당 isbn 책 없음", "NOT_FOUNDED_ISBN");
        }

        log.info("/books/??? End");

        if (bookDto != null) {
            log.info("ISBN : {}", bookDto.getIsbn());
            log.info("Title : {}", bookDto.getTitle());
            log.info("Author name : {}", bookDto.getAuthor_name());
            log.info("Publisher name : {}", bookDto.getPublisher_name());
            log.info("Thumbnail : {}", bookDto.getThumbnail());
            log.info("Description : {}", bookDto.getDescription());
            log.info("Published_date : {}", bookDto.getPublished_date());
            log.info("Created at : {}", bookDto.getCreated_at());
            log.info("Status : {}", bookDto.getStatus());
        }

        return ResponseEntity.ok(new ApiResponse(200, "도서 상세 조회 성공", bookDto));
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
        private BookStatus status;
    }

}
