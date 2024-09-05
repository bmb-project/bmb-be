package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    // Specification과 Pageable을 사용하는 메소드가 자동으로 제공됩니다.
    Book findByIsbn(String isbn);
    List<Book> findListByIsbn(String isbn);
    List<Book> findByIsbnIn(List<String> isbns);

    void deleteByIsbn(String isbn);
}
