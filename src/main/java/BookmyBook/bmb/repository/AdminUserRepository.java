package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}
