package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.repository.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import BookmyBook.bmb.response.ApiResponse;
import BookmyBook.bmb.response.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(user.getPassword())); //비밀번호 암호화

        userRepository.save(user);
        return user;
    }

    //user_id 중복 체크
    private void validateDuplicateUser(User user) {
        List<User> findUsersById = userRepository.findByUserID(user.getUser_id());
        if(!findUsersById.isEmpty()){
            throw new ExceptionResponse(409,"id 중복", "ID_DUPLICATION");
        }

        List<User> findUsersbyNickname = userRepository.findByNickname(user.getNickname());
        if(!findUsersbyNickname.isEmpty()){
            throw new ExceptionResponse(409,"nickname 중복", "NICKNAME_DUPLICATION");
        }
    }

    //글자 수 제한
    private void validteFormat(User user){
        Matcher matcherID = USER_ID_PATTERN.matcher(user.getUser_id());
        if(!matcherID.matches()){
            throw new ExceptionResponse(400, "id 형식에 맞지 않습니다", "INVALID_ID_FORMAT");
        }

        Matcher matcherName = NICKNAME_PATTERN.matcher(user.getNickname());
        if(!matcherName.matches()){
            throw new ExceptionResponse(400, "nickname 형식에 맞지 않습니다", "INVALID_NICKNAME_FORMAT");
        }

        Matcher matcherPassword = PASSWORD_PATTERN.matcher(user.getPassword());
        if(!matcherPassword.matches()){
            throw new ExceptionResponse(400, "password 형식에 맞지 않습니다", "INVALID_PASSWORD_FORMAT");
        }
    }

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * 로그인
     **/
    @Transactional
    public User login (User user){
        User login = checkExistUser(user);

        return login;
    }

    //회원 등록 여부 확인
    public User checkExistUser(User user){
        // 사용자가 입력한 비밀번호를 직접적으로 암호화하지 않음
        List<User> findUsersById = userRepository.findByUserID(user.getUser_id());
        if(findUsersById.isEmpty()){
            throw new ExceptionResponse(401,"로그인 실패", "INVALID_CREDENTIALS");
        }

        User findUser = findUsersById.get(0);
        //저장된 암호화 비밀번호와 입력된 비밀번호 비교
        if(!passwordEncoder.matches(user.getPassword(), findUser.getPassword())){
            throw new ExceptionResponse(401,"로그인 실패", "INVALID_CREDENTIALS");
        }

        return findUser;
    }

    /**
     * 회원 정보 조회
     */
    public User findOne(String userId){
        List<User> findUsersById = userRepository.findByUserID(userId);

        if(findUsersById.isEmpty()){
            throw new ExceptionResponse(404, "회원 조회 실패", "FAIL_TO_LOAD");
        }

        User findOne = findUsersById.get(0);
        return findOne;
    }

    //회원 전체 조회
    public List<User> findUsers(){
        return userRepository.findAll();
    }


}