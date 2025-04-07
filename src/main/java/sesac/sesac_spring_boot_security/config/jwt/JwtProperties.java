package sesac.sesac_spring_boot_security.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component // @Bean 으로 대체 가능? 의존성 주입을 위한 어노테이션
@ConfigurationProperties("jwt") // application.properties 참고하여 프로퍼티값 가져와서 사용
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
