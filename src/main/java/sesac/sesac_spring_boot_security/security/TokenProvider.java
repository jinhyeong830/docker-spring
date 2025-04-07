package sesac.sesac_spring_boot_security.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sesac.sesac_spring_boot_security.config.jwt.JwtProperties;
import sesac.sesac_spring_boot_security.entity.UserEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component // service 어노테이션과 같이 하위 메스
// @Slf4j // Simple Logging Facade for Java, log.info("...") log.warn() 등으로 로그의 단계를 나눠서 출력할 수 있다.
public class TokenProvider {
    // [before] 임의로 secret key 생성
    //  private static final String SECRET_KEY = "sesac-springboot-12341234";

    // [after] JwtProperties 클래스 이용해 설정 파일(application.properties) 값 꺼내오기
    @Autowired
    private JwtProperties jwtProperties;

    // "토큰 생성"
    public String create(final UserEntity userEntity){
        Date expiryDate =Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        // build.gradle 확인 - [io.jsonwebtoken:jjwt:0.9.1] implementation 되어 있어야 함
        return Jwts.builder()
                // 아래는 header 로 들어갈 내용
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey()) // 서명을 위한 알고리즘과 비밀키로 서명
                // 아래는 payload 로 들어갈 내용
                .setSubject(String.valueOf(userEntity.getId())) // sub: 토큰 제목 (id는 Long 타입이라 long to string 과정 거쳐서 인자로 전달)
                .setIssuer(jwtProperties.getIssuer()) // iss: 토큰 발급자
                .setIssuedAt(new Date()) // 토큰 발급 시간 (iat)
                .setExpiration(expiryDate) // 토큰 만료 시간 (exp)
                .compact(); // 토큰 생성
    }

    // 토큰을 받아서 "유효한 토큰인지 확인"하고 토큰에 있는 id 값을 반환하는 메서드
    public String validateAndGetUserId(String token){
        Claims claims = Jwts.parser() // jwt 파싱
                .setSigningKey(jwtProperties.getSecretKey())// 서명 검증을 위한 비밀키 (서명시와 같은 비밀키 사용)
                .parseClaimsJws(token) // 서명 검증 (검증할 때는 파싱, 비밀키설정 후 검증해야함)
                .getBody();

        /* 참고
        - parseClaimsJwt 메서드도 있는데, 해당 메서드는 서명을 포함하지 않는 토큰을 위한 메서드
        즉, 보안상 parseClaimsJws 메서드를 쓰는게 좋음.
        * */
        return claims.getSubject(); // payload 에서 setSubject 로 설정한 sub 값 가져오기 (토큰 생성시 `user.id` 를 `sub`으로 설정)
    }
}
