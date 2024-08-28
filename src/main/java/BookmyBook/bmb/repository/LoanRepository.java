package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByIsbnAndReturnAtIsNull(String isbn);
    Loan findByIsbnAndReturnAtIsNull(String isbn);
    Loan findByIsbnAndReturnAtIsNullAndUserId(String isbn, String userId);
    List<Loan> findByIsbnIn(List<String> isbns);
}