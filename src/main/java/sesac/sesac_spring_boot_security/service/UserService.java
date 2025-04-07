package sesac.sesac_spring_boot_security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sesac.sesac_spring_boot_security.entity.UserEntity;
import sesac.sesac_spring_boot_security.repository.UserRepository;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    // 회원가입
    public UserEntity create(final UserEntity userEntity){
        // 유효성검사 1) userEntity 혹은 email 이 null 인 경우 예외를 던짐
        if (userEntity == null || userEntity.getEmail() == null){
           throw new RuntimeException("Invalid arguments"); // 추후에 400 에러로 변경할 수 있어야 함..
        }

        // 유효성검사2) 이메일이 이미 존재하는 경우 예외를 던짐 (email 필드는 유니크 해야 함)
        final String email = userEntity.getEmail();

        if (userRepository.existsByEmail(email)){ // DB에 접근하여 받아온 이메일이 DB 에 있는지 확인
            log.warn("Email already exists {}", email); // 이메일 있으면 가입 제한
            throw new RuntimeException("Email already exists"); // 추후에 runtime 말고 다른것으로.. 바꾸자.
        }

        // email 중복체크 성공하면, userEntity DB 에 저장
        return userRepository.save(userEntity);
    }

    ///// 로그인시 사용되는 서비스로직
    // 암호화 하기 전
    // public UserEntity getByCredentials1(final String email, final String password){
    //     return userRepository.findByEmailAndPassword(email, password);
    // }
    // 암호화 후
    // PasswordEncoder: spring security 에서 안전한 비밀번호 처리를 위해 제공하는 interface
    public UserEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder){
        final UserEntity originalUser = userRepository.findByEmail(email); // 해당 email 가진 user 일단 찾기 (email unique 이기 때문에 하나이거나 0개)

        // encoder.matches
        // 복호화해서 비교하는 것이 아니고, 평문과 해시값을 비교하여 boolean 값 반환
        if(originalUser !=null && encoder.matches(password, originalUser.getPassword())){
            return originalUser;
        }
        return null;
    }
}
