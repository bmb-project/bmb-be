package BookmyBook.bmb.repository;

import BookmyBook.bmb.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user){
        em.persist(user);
    }

    //ID 조회
    public List<User> findByUserID(String UserId){
        return em.createQuery("select u from User u where u.user_id = :UserId", User.class)
                .setParameter("UserId", UserId)
                .getResultList();
    }

    //nickname 조회
    public List<User> findByNickname(String nickname){
        return em.createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", nickname)
                .getResultList();
    }


    public User findByUserIDKim(String UserId){
        return em.createQuery("select u from User u where u.user_id = :UserId", User.class)
                .setParameter("UserId", UserId)
                .getSingleResult();
    }

}