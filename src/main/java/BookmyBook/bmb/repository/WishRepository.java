package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @Query("SELECT w.book.id, COUNT(w.id) FROM Wish w WHERE w.book.id IN :bookIds GROUP BY w.book.id")
    List<Object[]> countWishesByBookIds(@Param("bookIds") List<Long> bookIds);

    @Query("SELECT COUNT(w) > 0 FROM Wish w WHERE w.book.id = :bookId AND w.user.user_id = :userId")
    boolean existsByBookIdAndUserId(@Param("bookId") Long bookId, @Param("userId") String userId);

}
