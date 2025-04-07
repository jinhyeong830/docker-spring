package sesac.sesac_spring_boot_security.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // @Component 는 내부 @Bean 간 호출 시 싱글톤이 깨질 수 있지만, @Configuration 은 프록시 기반으로 싱글톤을 보장해준다.
// 아래 passwordEncoder 는 전체 프로젝트에서 공통적으로 사용되므로, 싱글톤 Bean 으로 등록하기 위해 @Configuration 클래스에 정의
public class SecurityConfig {
    @Bean // 참고: @bean 은 @configuration 어노테이션과 함께 사용된다.
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // BCrypt 해시 알고리즘을 이용해 비밀번호 암호화
    }
}
