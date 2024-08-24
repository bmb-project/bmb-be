package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @Query("SELECT w.book.isbn, COUNT(w.id) FROM Wish w WHERE w.book.isbn IN :isbns GROUP BY w.book.isbn")
    List<Object[]> countWishesByBookIds(@Param("isbns") List<String> isbns);

    @Query("SELECT COUNT(w) > 0 FROM Wish w WHERE w.book.isbn = :isbn AND w.user.user_id = :userId")
    boolean existsByBookIdAndUserId(@Param("isbn") String  isbn, @Param("userId") String userId);

}
