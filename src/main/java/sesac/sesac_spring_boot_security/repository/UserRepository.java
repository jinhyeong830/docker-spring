package sesac.sesac_spring_boot_security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.sesac_spring_boot_security.entity.UserEntity;

/*
* 참고:
* JpaRepository 인터페이스를 상속받는 인터페이스는
* Spring Data JPA 가 자동으로 구현체를 생성하고, Bean 으로 등록한다.
*
* @Component, @Repository, @Service 등의 어노테이션이 없어도
* 다른 클래스에서 @Autowired 를 통한 의존성 주입을 사용할 수 있다.
*  */
public interface UserRepository extends JpaRepository<UserEntity, Long> { // 접근하고 싶은 엔티티, pk의 타입
    UserEntity findByEmail(String email); // email 기준으로 User 찾기
    Boolean existsByEmail(String email); // email 기준으로 User 가 있는지 없는지 여부 반환
    UserEntity findByEmailAndPassword(String email, String password); // 이메일, 패스워드가 모두 일치하는 User 찾기
}
