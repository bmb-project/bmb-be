package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookSpecification;
import BookmyBook.bmb.domain.Loan;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.LoanRepository;
import BookmyBook.bmb.repository.WishRepository;
import BookmyBook.bmb.response.AdminBookResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.dto.AdminBookDto;
import BookmyBook.bmb.response.dto.AdminLoanDto;
import BookmyBook.bmb.response.dto.BookDetail_DTO;
import BookmyBook.bmb.security.JwtUtil;
import jakarta.persistence.EntityManager;
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
public class AdminService {

    private final EntityManager entityManager;
    private final BookRepository bookRepository;
    private final WishRepository wishRepository;
    private final LoanRepository loanRepository;
    private final UserService userService;
    private final LoanService loanService;
    private final JwtUtil jwtUtil;

    //admin 도서 목록 조회
    public AdminBookResponse getAdminBooks(int page, int size, String category, String keyword, String token){

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

        //대여 기록 가져오기
        List<Loan> loans = loanRepository.findByIsbnIn(isbns);

        //도서 status 업데이트
        loanService.updateBookStatus(user_id, isbns);

        // BookDto로 변환
        List<AdminBookDto> bookDtos = books.stream().map(book -> {
            Long wishCount = wishCountMap.getOrDefault(book.getIsbn(), 0L);
            boolean wished = user_id != null && wishRepository.existsByBookIdAndUserId(book.getIsbn(), user_id);

            // 해당 도서의 대출 기록을 가져오기
            List<AdminLoanDto> loanDtos = loans.stream()
                    .filter(loan -> loan.getIsbn().equals(book.getIsbn()))
                    .map(loan -> new AdminLoanDto(
                            loan.getId(),
                            loan.getUserId(),
                            userService.getUsernameById(loan.getUserId()), // Fetch the username
                            loan.getLoan_at(),
                            loan.getReturnAt()
                    ))
                    .collect(Collectors.toList());

            return new AdminBookDto(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getThumbnail(),
                    book.getAuthor_name(),
                    book.getPublisher_name(),
                    book.getStatus(),
                    wishCount,
                    wished,
                    loanDtos
            );
        }).collect(Collectors.toList());

        int total_pages = 1;
        if(bookPage.getTotalPages() != 0) total_pages = bookPage.getTotalPages();

        // 응답 객체 생성
        AdminBookResponse response = new AdminBookResponse();
        response.setTotal_pages(total_pages);
        response.setCurrent_page(pageable.getPageNumber() + 1);
        response.setPage_size(pageable.getPageSize());
        response.setTotal_items(bookPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setBooks(bookDtos);

        return response;
    }

    //도서 추가
    @Transactional
    public boolean insert(Book book) {
        log.info("adminService - insert Start & End");

        Book b2 = bookRepository.save(book);
        if(b2.getId() == null){
            log.info("도서 등록 실패");
            return false;
        }else{
            log.info("도서 등록 성공");
            return true;
        }

    }

    //도서 삭제
    @Transactional
    public void delete(String isbn){
        log.info("qwer");
        Book book = bookRepository.findByIsbn(isbn);
        log.info("asdf");
        if(book == null){
            throw new ExceptionResponse(404, "해당 도서 없음", "NOT_FOUND_BOOK");
        }
        boolean tf = loanRepository.existsByIsbnAndReturnAtIsNull(isbn);
        if(tf){
            throw new ExceptionResponse(500, "도서 삭제 실패 - 대여 중인 도서 삭제 시도", "NOT_EXIST_BOOK_ADMIN");
        }
        bookRepository.deleteByIsbn(book.getIsbn());
        entityManager.flush();
        log.info("삭제 완료.");
    }

    //도서 겟또
    public BookDetail_DTO bring(String isbn){
        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new ExceptionResponse(404, "해당 도서 없음", "NOT_FOUND_BOOK");
        }
        BookDetail_DTO dto = new BookDetail_DTO(book.getIsbn(), book.getTitle(),
                book.getDescription(), book.getThumbnail(), book.getAuthor_name(),
                book.getPublisher_name(), book.getPublished_date(), book.getCreated_at(), book.getStatus());
        log.info("겟또 완료.");
        return dto;
    }

    // 도서 존재 확인
    public void youHere(String isbn){
        Book book = bookRepository.findByIsbn(isbn);
        if(book != null){
            throw new ExceptionResponse(404, "해당 도서가 이미 존재함", "ALREADY_EXIST_BOOK_ADMIN");
        }
    }

}
