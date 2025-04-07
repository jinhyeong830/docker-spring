package sesac.sesac_spring_boot_security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /*
    * OncePerRequestFilter 클래스 from :
    * - 한 요청당 반드시 한 번만 실행됨
    * - (인증 부분만 구현하고 유효시간 검사는 생략헸다고 함..)
    * - doFilterInternal 라는 이름으로 추상메서드로 가지고 있기 때문에 상속받는 JwtAuthenticationFilter 클래스에서 구현해야함 (@Override)
    *  */
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        // 요청과 응답, filter 단..
        try {
            String token = parseBearerToken(request); // http request 에서 token 가져오기 (or null)
            log.info("Filter is running....!");

            // token 검사
            // equalsIgnoreCase: 대소문자 무시하고 문자열이 서로 같은지 비교 >> token 과 "null" 문자열 비교
            if (token!=null && !token.equalsIgnoreCase("null")){
                String userId = tokenProvider.validateAndGetUserId(token); // 토큰 유효성 검증 + 해당 id 가져오기
                log.info("Authenticated user Id: " + userId);

                // 추출한 userId 를 이용해서 인증객체 생성
                /*
                * AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(주체, 자격증명, 권한정보);
                * 주체: 일반적으로 claims 의 sub 값, 현재는 userId
                * 자격증명: 비밀번호 혹은 null (null 이 더 안전하다고 함. said GPT_)
                * 권한정보: 해당 userId를 가진 user 의 권한 정보 (admin, user, .. )
                * */
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);

                // 인증 객체(authentication) 에 요청의 상세정보(detail) 추가: 클라이언트 ip, sessionID
                // - buildDetails: request(HttpServletRequest) 분석해서 추가할 details 생성
                authentication.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));

                // SecurityContext: 현재 사용자의 보안 컨텍스트(SecurityContext)를 저장하고 관리하는 전역 저장소
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); // 빈 저장소 생성
                securityContext.setAuthentication(authentication); // 인증정보를 저장소에 저장
                SecurityContextHolder.setContext(securityContext); // 생성한 보안 컨텍스트를 현재 스레드에 등록
            }else{
                log.info("token is null");
            }
        }
        catch (Exception ex){
            logger.error("could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);

    }

    // 해당 클래스에서만 쓰이는 함수이기 때문에 private 으로 선언
    private String parseBearerToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization"); // http 요청에서, header 의 Authorization 값 가져오기

        /*
        * StringUtils.hasText(문자열)
        *   - spring 에서 사용하는 문자열 검증 함수(SpringUtils 가 import 되어 있어야 사용 가능하다. Java 표준엔 없는 기능)
        *   - 검사하는 문자열이 -> null, ""(빈 문자열), "   "(공백만 있는 문자열) 이라면 false 반환
        * 문자열1.startsWith(문자열2)
        *   - 문자열1이 문자열2로 시작되면 true
        *   - ex) "example".startsWith("ex") => true
        *  */
        // bearerToken 이 빈 값이 아니고, Bearer 로 시작한다면
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 접두사 제거 (JWT 값만 추출)
            // Bearer 6 글자 + 공백 1 글자 = 총 7 글자, 앞에서부터 7자 빼고 새로운 문자열을 반환한다.
        }
        return null;
    }
}