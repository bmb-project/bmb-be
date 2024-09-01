package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookStatus;
import BookmyBook.bmb.domain.Loan;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.LoanRepository;
import BookmyBook.bmb.response.ExceptionResponse;
import BookmyBook.bmb.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoanService {

    @Autowired
    private final LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    //도서 대여
    @Transactional
    public Loan loanBook(String isbn, String token) {
        //book_isbn null 확인
        if(isbn == null){
            throw new ExceptionResponse(400, "book_isbn의 값이 필요", "UNDEFINED_ISBN");
        }

        //해당 도서 여부 조회
        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new ExceptionResponse(404, "해당 도서 없음", "NOT_FOUND_BOOK");
        }

        //해당도서의 대출여부확인
        if (loanRepository.existsByIsbnAndReturnAtIsNull(isbn)) {
            throw new ExceptionResponse(409, "이미 대출된 도서", "ALREADY_CHECKED_OUT");
        }

        //user_id 추출
        String userId = jwtUtil.getUserId(token, "access");

        try{
            Loan loan = new Loan();
            loan.setIsbn(isbn);
            loan.setUserId(userId);
            loan.setLoan_at(LocalDateTime.now());
            loan.setReturnAt(null);
            return loanRepository.save(loan);

        } catch (Exception e){
            throw new ExceptionResponse(500, "대여 처리 실패", "FAIL_TO_LOAN");
        }

    }

    //대출여부에 따른 status 업데이트
    @Transactional
    public void updateBookStatus(String user_id, List<String> isbns){
        //모든 도서 상태를 AVALIABLE로 초기화
        List<Book> books = bookRepository.findByIsbnIn(isbns);
        for (Book book: books){
            BookStatus status = BookStatus.AVAILABLE;
            //현재 대출 중인 도서 확인
            Loan loan = loanRepository.findByIsbnAndReturnAtIsNull(book.getIsbn());
            if(loan != null){
                if(loan.getUserId().equals(user_id)) status = BookStatus.CHECKED_OUT;
                else status = BookStatus.UNAVAILABLE;
            }
            book.setStatus(status);
        }
        bookRepository.saveAll(books);
    }

    //대출여부에 따른 status 업데이트
    @Transactional
    public void updateBookStatus(String user_id, String isbn){
        //모든 도서 상태를 AVALIABLE로 초기화
        List<Book> books = bookRepository.findListByIsbn(isbn);
        if(books.isEmpty()) throw new ExceptionResponse(404, "해당 isbn의 책 없음", "NOT_FOUNDED_ISBN");

        for (Book book: books){
            BookStatus status = BookStatus.AVAILABLE;
            //현재 대출 중인 도서 확인
            Loan loan = loanRepository.findByIsbnAndReturnAtIsNull(book.getIsbn());
            if(loan != null){
                if(loan.getUserId().equals(user_id)) status = BookStatus.CHECKED_OUT;
                else status = BookStatus.UNAVAILABLE;
            }
            book.setStatus(status);
        }
        bookRepository.saveAll(books);
    }

    //도서반납
    @Transactional
    public Loan returnBook(String isbn, String token){
        //user_id 추출
        String tokenUser_id = jwtUtil.getUserId(token, "access");

        //대여 정보 조회
        Loan loan = loanRepository.findByIsbnAndReturnAtIsNullAndUserId(isbn, tokenUser_id);
        if(loan == null){
            throw new ExceptionResponse(404, "대출 기록 또는 권한 없음", "NOT_FOUND_RECORD");
        }

        //return_at 업데이트
        try{
            loan.setReturnAt(LocalDateTime.now());
            return loanRepository.save(loan);
        }catch (Exception e){
            throw new ExceptionResponse(500, "반납 처리 실패", "FAIL_TO_RETURN");
        }
    }
}