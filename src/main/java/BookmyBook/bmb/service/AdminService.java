package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookSpecification;
import BookmyBook.bmb.domain.Loan;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.LoanRepository;
import BookmyBook.bmb.repository.WishRepository;
import BookmyBook.bmb.response.AdminBookResponse;
import BookmyBook.bmb.response.dto.AdminBookDto;
import BookmyBook.bmb.response.dto.AdminLoanDto;
import BookmyBook.bmb.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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
public class AdminService {

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

        String user_id = jwtUtil.getUserId(token);

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

        // 응답 객체 생성
        AdminBookResponse response = new AdminBookResponse();
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(pageable.getPageNumber() + 1);
        response.setPageSize(pageable.getPageSize());
        response.setTotalItems(bookPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setBooks(bookDtos);

        return response;
    }
}
