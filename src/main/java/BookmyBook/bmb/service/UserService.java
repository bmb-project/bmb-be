package BookmyBook.bmb.service;


import BookmyBook.bmb.domain.*;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.LoanRepository;
import BookmyBook.bmb.repository.UserRepository;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import BookmyBook.bmb.repository.WishRepository;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.response.UserLoanResponse;
import BookmyBook.bmb.response.UserWishResponse;
import BookmyBook.bmb.response.dto.UserDto;
import BookmyBook.bmb.response.dto.UserLoanDto;
import BookmyBook.bmb.response.dto.UserWishDto;
import BookmyBook.bmb.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final WishRepository wishRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoanService loanService;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtUtil jwtUtil,
                       BookRepository bookRepository, LoanRepository loanRepository, WishRepository wishRepository, LoanService loanService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.wishRepository = wishRepository;
        this.loanService = loanService;
    }

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{4,10}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[ぁ-ゔァ-ヴー一-龯a-zA-Z0-9]{2,10}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?:(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,15}|(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15})$");

    /**
     * 회원가입
     */
    @Transactional
    public User join(User user){

        validateDuplicateUser(user); //중복 회원 검증
        validteFormat(user); //형식 검사
        user.setPassword(passwordEncoder.encode(user.getPassword())); //비밀번호 암호화

        userRepository.save(user);
        return user;
    }

    //user_id 중복 체크
    private void validateDuplicateUser(User user) {
        List<User> findUsersById = userRepository.findByUserID(user.getUser_id());
        if(!findUsersById.isEmpty()){
            throw new ExceptionResponse(409,"id 중복", "ID_DUPLICATION");
        }

        List<User> findUsersbyNickname = userRepository.findByNickname(user.getNickname());
        if(!findUsersbyNickname.isEmpty()){
            throw new ExceptionResponse(409,"nickname 중복", "NICKNAME_DUPLICATION");
        }
    }

    //글자 수 제한
    private void validteFormat(User user) {
        Matcher matcherID = USER_ID_PATTERN.matcher(user.getUser_id());
        if (!matcherID.matches()) {
            throw new ExceptionResponse(400, "id 형식에 맞지 않습니다", "INVALID_ID_FORMAT");
        }

        Matcher matcherName = NICKNAME_PATTERN.matcher(user.getNickname());
        if (!matcherName.matches()) {
            throw new ExceptionResponse(400, "nickname 형식에 맞지 않습니다", "INVALID_NICKNAME_FORMAT");
        }

        Matcher matcherPassword = PASSWORD_PATTERN.matcher(user.getPassword());
        if (!matcherPassword.matches()) {
            throw new ExceptionResponse(400, "password 형식에 맞지 않습니다", "INVALID_PASSWORD_FORMAT");
        }
    }

    /**
     * 로그인
     **/
    @Transactional
    public User login (User user){

        return checkExistUser(user);
    }

    //회원 등록 여부 확인
    public User checkExistUser(User user){
        List<User> findUsersById = userRepository.findByUserID(user.getUser_id());
        if(findUsersById.isEmpty()){
            throw new ExceptionResponse(401,"로그인 실패", "INVALID_ID_OR_PASSWORD");
        }

        User findUser = findUsersById.getFirst();
        //저장된 암호화 비밀번호와 입력된 비밀번호 비교
        if(!passwordEncoder.matches(user.getPassword(), findUser.getPassword())){
            throw new ExceptionResponse(401,"로그인 실패", "INVALID_ID_OR_PASSWORD");
        }

        return findUser;
    }

    /**
     * 대여 목록 조회
     */
    public UserLoanResponse getUserLoan(int page, int size, String category, String keyword, String token){
        //user_id 추출
        String tokenUser_id = jwtUtil.getUserId(token, "access");

        //페이징 요청에 따른 페이징 처리
        Pageable pageable = PageRequest.of(page - 1, size);

        //검색 조건 설정
        Specification<Book> spec = BookSpecification.byCategoryAndKeyword(category, keyword);

        //도서 목록 조회
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        List<Book> books = bookPage.getContent();

        // 도서 Isbn 리스트 가져오기
        List<String> isbns = books.stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());

        //도서 status 업데이트
        loanService.updateBookStatus(tokenUser_id, isbns);

        // CHECKEDOUT 상태인 책만 필터링
        books = books.stream()
                .filter(book -> BookStatus.CHECKED_OUT.equals(book.getStatus()))
                .toList();

        // 도서의 Isbn 리스트
        isbns = books.stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());

        // 대여 기록 조회
        List<Loan> loans = loanRepository.findByIsbnIn(isbns);

        // ISBN을 키로 하는 대여 기록 맵 생성 (중복 처리)
        Map<String, Loan> loanMap = loans.stream()
                .collect(Collectors.toMap(
                        Loan::getIsbn,
                        loan -> loan,
                        (existing, replacement) -> replacement  // 중복된 경우 대체
                ));

        // BookDto로 변환
        List<UserLoanDto> bookDtos = books.stream().map(book -> {
            Loan loan = loanMap.get(book.getIsbn());
            return new UserLoanDto(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getThumbnail(),
                    book.getAuthor_name(),
                    book.getPublisher_name(),
                    book.getStatus(),
                    loan.getLoan_at(),
                    loan.getReturnAt()

            );

        }).toList();

        // 응답 객체 생성
        UserLoanResponse response = new UserLoanResponse();
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(pageable.getPageNumber() + 1);
        response.setPageSize(pageable.getPageSize());
        response.setTotalItems(bookPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setBooks(bookDtos);

        return response;
    }

    /**
     * 좋아요 목록 조회
     */
    public UserWishResponse getUserWish(int page, int size, String category, String keyword, String token){
        //user_id 추출
        String tokenUser_id = jwtUtil.getUserId(token, "access");

        //페이징 요청에 따른 페이징 처리
        Pageable pageable = PageRequest.of(page - 1, size);

        //검색 조건 설정
        Specification<Book> spec = BookSpecification.byCategoryAndKeyword(category, keyword);

        //도서 목록 조회
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        List<Book> books = bookPage.getContent();

        // 도서 Isbn 리스트 가져오기
        List<String> isbns = books.stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());

        //도서 status 업데이트
        loanService.updateBookStatus(tokenUser_id, isbns);

        // Wish 목록 조회
        List<Wish> wishes = wishRepository.findByUserIdAndIsbns(tokenUser_id, isbns);

        // ISBN을 리스트로 변환
        List<String> isbnList = wishes.stream()
                .map(wish -> wish.getBook().getIsbn()).distinct().collect(Collectors.toList());

        // ISBN으로 도서 목록 조회
        List<Book> wishedBooks = bookRepository.findByIsbnIn(isbnList);

        // 도서 정보를 UserWishDto로 변환
        List<UserWishDto> wishDtos = wishedBooks.stream().map(book -> new UserWishDto(
                book.getIsbn(),
                book.getTitle(),
                book.getThumbnail(),
                book.getAuthor_name(),
                book.getPublisher_name(),
                book.getStatus()

        )).toList();

        // 응답 객체 생성
        UserWishResponse response = new UserWishResponse();
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(pageable.getPageNumber() + 1);
        response.setPageSize(pageable.getPageSize());
        response.setTotalItems(bookPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setBooks(wishDtos);

        return response;
    }

    //username 조회
    public String getUsernameById(String userId) {
        List<User> UserId = userRepository.findByUserID(userId);
        if(userId.isEmpty()) throw new ExceptionResponse(404, "회원 조회 실패", "FAIL_TO_LOAD");

        return UserId.getFirst().getNickname();
    }

    //Role 조회
    public UserRole getRoleById(String userId){
        List<User> UserId = userRepository.findByUserID(userId);
        if(userId.isEmpty()) throw new ExceptionResponse(404, "회원 조회 실패", "FAIL_TO_LOAD");

        return UserId.getFirst().getRole();
    }

    //회원정보조회
    public UserDto getUserInfo(String userId){
        List<User> users = userRepository.findByUserID(userId);

        return new UserDto(userId, users.getFirst().getNickname(), users.getFirst().getRole());
    }
}