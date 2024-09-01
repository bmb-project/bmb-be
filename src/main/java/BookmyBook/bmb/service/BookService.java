package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookSpecification;
import BookmyBook.bmb.domain.Wish;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.WishRepository;
import BookmyBook.bmb.response.BookResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.dto.BookDetailNoID_DTO;
import BookmyBook.bmb.response.dto.BookDto;
import BookmyBook.bmb.response.dto.BookDetail_DTO;
import BookmyBook.bmb.response.dto.WishDto;
import BookmyBook.bmb.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final WishRepository wishRepository;
    private final LoanService loanService;
    private final JwtUtil jwtUtil;

    //도서 목록 조회
    public BookResponse getBooks(int page, int size, String category, String keyword, String token){
        //페이징 요청에 따른 페이징 처리
        Pageable pageable = PageRequest.of(page - 1, size);

        //검색 조건 설정
        Specification<Book> spec = BookSpecification.byCategoryAndKeyword(category, keyword);

        String user_id = jwtUtil.getUserId(token, "access");

        //도서 목록 조회
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        List<Book> books = bookPage.getContent();

        // 도서 Isbn 리스트 가져오기
        List<String> isbns = books.stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());

        // 찜 수를 가져오기
        List<Object[]> wishCounts = wishRepository.countWishesByBookIds(isbns);
        Map<String , Long> wishCountMap = wishCounts.stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));

        //도서 status 업데이트
        loanService.updateBookStatus(user_id, isbns);

        // BookDto로 변환
        List<BookDto> bookDtos = books.stream().map(book -> {
            Long wishCount = wishCountMap.getOrDefault(book.getIsbn(), 0L);
            boolean wished = user_id != null && wishRepository.existsByBookIdAndUserId(book.getIsbn(), user_id);
            return new BookDto(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getThumbnail(),
                    book.getAuthor_name(),
                    book.getPublisher_name(),
                    book.getStatus(),
                    wishCount,
                    wished
            );
        }).collect(Collectors.toList());


        // 응답 객체 생성
        BookResponse response = new BookResponse();
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(pageable.getPageNumber() + 1);
        response.setPageSize(pageable.getPageSize());
        response.setTotalItems(bookPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setBooks(bookDtos);

        return response;
    }

    //도서별 좋아요 조회
    public WishDto getBookWish(String token, String isbn){
        String user_id = jwtUtil.getUserId(token, "access");

        //도서 status 업데이트
        loanService.updateBookStatus(user_id, isbn);

        // 찜 수 가져오기
        Long wishCount = wishRepository.countWishesByIsbn(isbn);

        // 사용자가 찜했는지 여부 확인
        boolean wished = user_id != null && wishRepository.existsByBookIdAndUserId(isbn, user_id);

        return new WishDto(
                wishCount,
                wished
        );
    }

    //좋아요 하기
    @Transactional
    public WishDto saveWish(String isbn, String token){
        String user_id = jwtUtil.getUserId(token, "access");

        try {
            boolean wishExists = wishRepository.existsByBookIdAndUserId(isbn, user_id);
            if(wishExists) throw new ExceptionResponse(409, "좋아요 실패", "WISH_ALREADY_EXISTS");

            // Create a new Wish entity
            Wish wish = new Wish();
            wish.setUserId(user_id);
            wish.setIsbn(isbn);
            wish.setAdded_at(LocalDateTime.now());

            // Save the new wish
            wishRepository.save(wish);

            //도서 status 업데이트
            loanService.updateBookStatus(user_id, isbn);

            // 찜 수 가져오기
            Long wishCount = wishRepository.countWishesByIsbn(isbn);

            // 사용자가 찜했는지 여부 확인
            boolean wished = user_id != null && wishRepository.existsByBookIdAndUserId(isbn, user_id);

            return new WishDto(
                    wishCount,
                    wished
            );
        }catch (DataIntegrityViolationException e){
            throw new ExceptionResponse(400, "잘못된 isbn", "INVALID_ISBN");
        }
    }

    //좋아요 취소
    @Transactional
    public WishDto deleteWish(String isbn, String token){
        String user_id = jwtUtil.getUserId(token, "access");

        try {
            boolean wishExists = wishRepository.existsByBookIdAndUserId(isbn, user_id);
            if(!wishExists) throw new ExceptionResponse(404, "좋아요 취소 실패", "WISH_NOT_FOUND");

            // Delete the wish
            wishRepository.deleteByBookIdAndUserId(isbn, user_id);

            // Get the updated number of wishes for the book
            Long wishCount = wishRepository.countWishesByIsbn(isbn);

            // 사용자가 찜했는지 여부 확인
            boolean wished = user_id != null && wishRepository.existsByBookIdAndUserId(isbn, user_id);

            return new WishDto(
                    wishCount,
                    wished
            );
        }catch (Exception e){
            throw new ExceptionResponse(400, "잘못된 isbn", "INVALID_ISBN");
        }
    }

    //도서 추가
    @Transactional
    public Book insert(Book book) {
        log.info("bookService - insert Start & End");
        return bookRepository.save(book);
    }

    //도서 한 권 상세정보
    public BookDetailNoID_DTO bookView(String isbn){

        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new ExceptionResponse(404, "해당 도서 없음", "NOT_FOUND_BOOK");
        }

        BookDetailNoID_DTO dto = new BookDetailNoID_DTO(book.getIsbn(), book.getTitle(),
                book.getDescription(), "src/main/resources/static/img/" + book.getThumbnail(), book.getAuthor_name(),
                book.getPublisher_name(), book.getPublished_date(), book.getCreated_at(), book.getStatus());

        return dto;
    }

    // 도서 존재 확인
    public boolean bookKakuninn(String isbn){
        log.info("bookKakuninn Start! : " + isbn);
        Book book = bookRepository.findByIsbn(isbn);

        // 13자리가 아니면 컷
        if(isbn.length() != 13){
            throw new ExceptionResponse(404, "잘못된 ISBN", "INVALID_ISBN");
        }else if(book == null){
            throw new ExceptionResponse(404, "해당 도서 없음", "NOT_FOUND_BOOK");
        }
        log.info("findByIsbn부터 isbn 확인 과정까지 이상 무.");
        if(book.getId() != null){
            log.info("해당 도서가 존재합니다.");
            return true;
        }else{
            log.info("해당 도서가 존재하지 않습니다.");
            return false;
        }
    }

}
