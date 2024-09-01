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
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
    public ResponseEntity<?> insertBook(@ModelAttribute CreateBookRequest request) {
        log.info("파일 이름 : {}", request.getThumbnail().toString());
        if (request.isbn.length() != 13) {
            throw new ExceptionResponse(400, "잘못된 ISBN", "INVALID_ISBN_ADMIN");
        }
        adminService.youHere(request.getIsbn());

        // 파일크기 제한을 걸기는 했는데, yml파일 선에서 알아서 컷 해주는 겉 같습니다. 이 조건문이 쓰이진 않아요.
        if(request.getThumbnail().getSize() > 1024 * 1024 * 5){
            throw new ExceptionResponse(407, "파일 크기 제한 5MB", "TOO_BIG_FILE_SIZE");
        }else{
            // 길면 보기 안 좋으니 fileType로 줄임.
            String fileType = request.getThumbnail().getContentType();
            log.info(fileType);
            if(fileType.equalsIgnoreCase("image/png") ||
                    fileType.equalsIgnoreCase("image/jpg") ||
                    fileType.equalsIgnoreCase("image/jpeg") ||
                    fileType.equalsIgnoreCase("image/webp") ||
                    fileType.equalsIgnoreCase("image/gif") ||
                    fileType.equalsIgnoreCase("image/bmp")){

            }else{
                throw new ExceptionResponse(406, "잘못된 파일 형식", "DO_NOT_USE_THIS_TYPE");
            }
        }

        try {
            Book book = new Book();
            book.setIsbn(request.getIsbn());
            book.setTitle(request.getTitle());
            book.setAuthor_name(request.getAuthor_name());
            book.setPublisher_name(request.getPublisher_name());
            book.setDescription(request.getDescription());
            book.setPublished_date(request.getPublished_date());
            book.setCreated_at(LocalDateTime.now());

            // 썸네일 이미지 파일 처리
            MultipartFile thumbnailFile = request.getThumbnail();
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                // 파일을 서버에 저장하고, 파일 경로 또는 URL을 데이터베이스에 저장
                String fileName = UUID.randomUUID().toString() + "_" + thumbnailFile.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/img/" + fileName);
                Files.copy(thumbnailFile.getInputStream(), path);

                book.setThumbnail(fileName);  // 저장된 파일의 경로 또는 URL을 설정
            }

            if (request.isAvailable()) {
                book.setStatus(BookStatus.AVAILABLE);
            } else {
                throw new ExceptionResponse(401, "available의 값이 true가 아닙니다.", "AVAILABLE_IS_NOT_TRUE");
            }

            boolean insert = adminService.insert(book);

            if (insert) {
                BookDetail_DTO bookDto = new BookDetail_DTO(book.getIsbn(), book.getTitle(),
                        book.getDescription(), book.getThumbnail(), book.getAuthor_name(),
                        book.getPublisher_name(), book.getPublished_date(), book.getCreated_at(), book.getStatus());

                return ResponseEntity.ok(new ApiResponse(201, "도서 등록 성공", bookDto));
            } else {
                return ResponseEntity.ok(new ApiResponse(404, "도서 등록 실패", ""));
            }

        } catch (Exception e) {
            throw new ExceptionResponse(410, "처리 과정 중 예외 발생", "EXCEPTION_ADMIN");
        }
    }

    @DeleteMapping("/books") // ID로 한 권 선택 삭제
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> viewDelete(@RequestBody CreateBookRequest request){
        String isbn = request.getIsbn();
        if(isbn.length() != 13){
            throw new ExceptionResponse(400, "잘못된 ISBN", "INVALID_ISBN_ADMIN");
        }
        log.info("/books Start : {}", isbn);

        // 도서 가져오기
        BookDetail_DTO bookDto = adminService.bring(isbn);

        // 도서가 존재하지 않을 경우 처리
        if (bookDto == null) {
            log.info("도서 ID {}에 해당하는 도서를 찾을 수 없습니다.", isbn);
            throw new ExceptionResponse(404, "도서 목록 및 정보 조회 실패", "FAIL_TO_LOAD_ADMIN");
        }else if(bookDto.getStatus() != BookStatus.AVAILABLE){
            log.info("선택한 도서는 대여 중인 도서이므로 삭제할 수 없습니다.");
            throw new ExceptionResponse(500, "도서 삭제 실패 - 대여 중인 도서 삭제 시도", "NOT_EXIST_BOOK_ADMIN");
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
        adminService.delete(bookDto.getIsbn());
        log.info("Delete Over");
        return ResponseEntity.ok(new ApiResponse(200, "도서 삭제 성공"));
    }

    @Data
    static class CreateBookRequest {
        private String isbn;
        private long id;
        private String title;
        private String author_name;
        private String publisher_name;
        private MultipartFile thumbnail;
        private String description;
        private LocalDate published_date;
        private LocalDateTime created_at;
        private boolean available;
    }

}
