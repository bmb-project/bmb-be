package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.exception.method.DuplicateException;
import BookmyBook.bmb.exception.method.VaildFormatException;
import BookmyBook.bmb.repository.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{4,10}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[ぁ-ゔァ-ヴー一-龯a-zA-Z0-9]{2,10}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?:(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,15}|(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15})$");

    /**
     * 회원가입
     */
    @Transactional
    public User join(User user){

        validateDuplicateUser(user); //중복 회원 검증
        validteFormat(user); //형식 검사

        userRepository.save(user);
        return user;
    }

    //user_id 중복 체크
    private void validateDuplicateUser(User user) {
        List<User> findUsersById = userRepository.findByUserID(user.getUser_id());
        if(!findUsersById.isEmpty()){
            throw new DuplicateException("id 중복", "ID_DUPLICATION");
        }

        List<User> findUsersbyNickname = userRepository.findByNickname(user.getNickname());
        if(!findUsersbyNickname.isEmpty()){
            throw new DuplicateException("nickname 중복", "NICKNAME_DUPLICATION");
        }
    }

    //글자 수 제한
    private void validteFormat(User user){
        Matcher matcherID = USER_ID_PATTERN.matcher(user.getUser_id());
        if(!matcherID.matches()){
            throw new VaildFormatException("id 형식에 맞지 않습니다", "INVALID_ID_FORMAT");
        }

        Matcher matcherName = NICKNAME_PATTERN.matcher(user.getNickname());
        if(!matcherName.matches()){
            throw new VaildFormatException("nickname 형식에 맞지 않습니다", "INVALID_NICKNAME_FORMAT");
        }

        Matcher matcherPassword = PASSWORD_PATTERN.matcher(user.getPassword());
        if(!matcherPassword.matches()){
            throw new VaildFormatException("password 형식에 맞지 않습니다", "INVALID_PASSWORD_FORMAT");
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