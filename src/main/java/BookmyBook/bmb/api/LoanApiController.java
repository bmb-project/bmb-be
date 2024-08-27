package BookmyBook.bmb.api;

import BookmyBook.bmb.domain.Loan;
import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.dto.LoanDto;
import BookmyBook.bmb.security.JwtUtil;
import BookmyBook.bmb.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoanApiController {

    private final JwtUtil jwtUtil;
    private final LoanService loanService;

    //도서 대여
    @PostMapping("/loan")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> loanBook(@RequestBody Map<String, String> request, HttpServletRequest httpRequest){

        //Cookie에서 Access Token 추출
        String accessToken = jwtUtil.getTokenFromCookies(httpRequest.getCookies(), "accessToken");
        String book_isbn = request.get("book_isbn");

        Loan loan = loanService.loanBook(book_isbn, accessToken);

        LoanDto loanDto = new LoanDto(loan.getIsbn(), loan.getUserId(), loan.getLoan_at(), loan.getReturnAt());
        return ResponseEntity.ok(new ApiResponse(201, "대여 성공", loanDto));
    }

    //도서 반납
    @PutMapping("/loan/{id}")
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public ResponseEntity<?> returnBook(@PathVariable("id") String user_id, @RequestBody Map<String, String> request, HttpServletRequest httpRequest){

        //Cookie에서 Access Token 추출
        String accessToken = jwtUtil.getTokenFromCookies(httpRequest.getCookies(), "accessToken");
        String book_isbn = request.get("book_isbn");


        Loan loan = loanService.returnBook(user_id, book_isbn, accessToken);

        LoanDto loanDto = new LoanDto(loan.getIsbn(), loan.getUserId(), loan.getLoan_at(), loan.getReturnAt());
        return ResponseEntity.ok(new ApiResponse(200, "도서 반납 성공", loanDto));
    }
}
