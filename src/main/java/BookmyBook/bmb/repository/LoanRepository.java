package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByIsbnAndReturnAtIsNull(String isbn);
    Loan findByIsbnAndReturnAtIsNull(String isbn);
}