package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(User user){

        validateDuplicateUser(user); //중복 회원 검증
        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateUser(User user) {
        List<User> findUsers = userRepository.findByName(user.getNickname());
        if(!findUsers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<User> findUsers(){
        return userRepository.findAll();
    }

    public User findOne(Long userId){
        return userRepository.findOne(userId);
    }
}