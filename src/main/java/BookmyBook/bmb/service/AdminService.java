package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.*;
import BookmyBook.bmb.repository.*;
import BookmyBook.bmb.response.AdminBookResponse;
import BookmyBook.bmb.response.AdminUesrsResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.dto.*;
import BookmyBook.bmb.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final EntityManager entityManager;
    private final BookRepository bookRepository;
    private final WishRepository wishRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final UserService userService;
    private final LoanService loanService;
    private final JwtUtil jwtUtil;

    //admin 도서 목록 조회
    public AdminBookResponse getAdminBooks(int page, int size, String category, String keyword, String token){

        //페이징 요청에 따른 페이징 처리
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));

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

    //도서 등록
    @Transactional
    public BookDetail_DTO insert(Book book) {
        //isbn 자릿수 체크
        if (book.getIsbn().length() != 13) {
            throw new ExceptionResponse(400, "잘못된 ISBN", "INVALID_ISBN");
        }

        //날짜 체크
        if(book.getPublished_date().isAfter(LocalDate.now())){
            throw new ExceptionResponse(400, "오늘 포함 이전만 입력 가능합니다", "INVALID_PUBLISHED_DATE");
        }

        //중복 도서 체크
        if(bookRepository.findByIsbn(book.getIsbn()) != null){
            throw new ExceptionResponse(409, "동일한 ISBN의 도서가 존재합니다", "BOOK_ALREADY_INSERT");
        }

        //description 글자수 체크
        if(book.getDescription().length() > 1000){
            throw new ExceptionResponse(400, "소개글은 1000자 이하로 입력 가능합니다", "INVALID_DESCRIPTION");
        }

        Book save = bookRepository.save(book);

        return new BookDetail_DTO(save.getIsbn(), save.getTitle(),
                save.getDescription(), save.getThumbnail(), save.getAuthor_name(),
                save.getPublisher_name(), save.getPublished_date(), save.getCreatedAt(), save.getStatus());
    }

    //도서 삭제
    @Transactional
    public void delete(String isbn){
        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new ExceptionResponse(404, "해당 isbn 책 없음", "NOT_FOUNDED_ISBN");
        }

        // 도서가 대여 중일 경우 삭제불가
        boolean tf = loanRepository.existsByIsbnAndReturnAtIsNull(isbn);
        if(tf){
            throw new ExceptionResponse(409, "대여 목록이 있어 삭제할 수 없습니다", "BOOK_HAS_LOANS");
        }
        bookRepository.deleteByIsbn(book.getIsbn());
        entityManager.flush();
    }

    //도서 겟또
    public BookDetail_DTO bring(String isbn){
        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new ExceptionResponse(404, "해당 isbn 책 없음", "NOT_FOUNDED_ISBN");
        }
        return new BookDetail_DTO(book.getIsbn(), book.getTitle(),
                book.getDescription(), book.getThumbnail(), book.getAuthor_name(),
                book.getPublisher_name(), book.getPublished_date(), book.getCreatedAt(), book.getStatus());
    }

    //도서 한 권 상세정보
    public BookDetailAdmin_DTO bookView(String isbn){

        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new ExceptionResponse(404, "해당 isbn 책 없음", "NOT_FOUNDED_ISBN");
        }

        List<Loan> rentList = loanRepository.findByIsbn(isbn);

        ArrayList<AdminLoanDto> al_adminLoanDTO = new ArrayList<>();

        for(Loan loann : rentList){
            User user = userRepository.findByUserIDKim(loann.getUserId());
            AdminLoanDto ald = new AdminLoanDto(loann.getId(), user.getNickname(), loann.getUserId(),
                                                loann.getLoan_at(), loann.getReturnAt());
            al_adminLoanDTO.add(ald);
        }

        return new BookDetailAdmin_DTO(book.getIsbn(), book.getTitle(), book.getDescription(), book.getThumbnail(),
                book.getAuthor_name(), book.getPublisher_name(), book.getPublished_date(),
                book.getStatus(), al_adminLoanDTO);
    }

    //회원 목록 및 대여 정보 조회
    public AdminUesrsResponse getUsersLoanList(int page, int size, String category, String keyword){
        // 페이징 설정: createdAt을 기준으로 정렬 (가입 순)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));

        //검색 조건 설정
        Specification<User> spec = UserSpecification.byCategoryAndKeyword(category, keyword);

        // 회원 목록 조회 (카테고리, 키워드 기반으로 필터링 가능)
        Page<User> userPage = adminUserRepository.findAll(spec, pageable);
        List<User> users = userPage.getContent();

        // 회원 ID 목록 추출
        List<String> userIds = users.stream()
                .map(User::getUser_id)
                .collect(Collectors.toList());

        // 해당 회원들의 대여 기록 조회
        List<Loan> loans = loanRepository.findByUserIdIn(userIds);

        // 대여 기록을 회원 ID별로 그룹화
        Map<String, List<AdminUserLoanDto>> loanMap = loans.stream()
                .collect(Collectors.groupingBy(Loan::getUserId,
                        Collectors.mapping(loan -> new AdminUserLoanDto(
                                loan.getId(),
                                loan.getIsbn(),
                                loan.getBook().getTitle(),
                                loan.getLoan_at(),
                                loan.getReturnAt()
                        ), Collectors.toList())
                ));

        // 회원에 대한 대여 정보 매핑
        List<AdminUsersDto> userDtos = users.stream().map(user -> {
            // 해당 회원의 대여 정보를 loanMap에서 조회
            List<AdminUserLoanDto> loanDtos = loanMap.getOrDefault(user.getUser_id(), Collections.emptyList());
            return new AdminUsersDto(user.getUser_id(), user.getNickname(), user.getCreatedAt(), loanDtos);
        }).collect(Collectors.toList());

        int total_pages = userPage.getTotalPages() > 0 ? userPage.getTotalPages() : 1;

        // 응답 객체 생성
        AdminUesrsResponse response = new AdminUesrsResponse();
        response.setTotal_pages(total_pages);
        response.setCurrent_page(pageable.getPageNumber() + 1);
        response.setPage_size(pageable.getPageSize());
        response.setTotal_items(userPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setUsers(userDtos);

        return response;
    }
}
