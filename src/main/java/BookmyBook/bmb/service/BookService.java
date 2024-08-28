package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookSpecification;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.WishRepository;
import BookmyBook.bmb.response.BookResponse;
import BookmyBook.bmb.response.dto.BookDto;
import BookmyBook.bmb.response.dto.WishDto;
import BookmyBook.bmb.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void saveItem(Book book){
        bookRepository.save(book);
    }

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

    //도서 추가
    @Transactional
    public Book insert(Book book) {
        log.info("bookService - insert Start & End");
        return bookRepository.save(book);
    }

    //도서 한 권 상세정보
    public BookDto view(long id){
        // 도서를 ID로 조회하고 Optional<Book>로 받음
        Book book = bookRepository.findById(id);
        BookDto dto = new BookDto(book.getIsbn(), book.getId(), book.getTitle(), book.getThumbnail(), book.getAuthor_name(),
                book.getPublisher_name(), book.getStatus(), book.getDescription(),
                book.getPublished_date(), book.getCreated_at());
        // Optional의 값이 존재하는 경우 DTO로 변환, 없으면 null 반환
        return dto;
    }

    //도서 삭제
    @Transactional
    public void delete(long id){
        Book book = bookRepository.findById(id);
        bookRepository.deleteById(id);
        log.info("삭제 완료.");
    }

}
