package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        User user = new User();
        user.setNickname("Kim");

        //when
        Long saveId = userService.join(user);

        //then
        Assertions.assertEquals(user, userRepository.findOne(saveId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        User user1 = new User();
        user1.setNickname("Kim");

        User user2 = new User();
        user2.setNickname("Kim");

        //when
        userService.join(user1);

        //then
        assertThrows(IllegalStateException.class, () ->
        { userService.join(user2); });
    }
}