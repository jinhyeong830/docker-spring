package sesac.sesac_spring_boot_security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.sesac_spring_boot_security.dto.ResponseDTO;
import sesac.sesac_spring_boot_security.dto.UserDTO;
import sesac.sesac_spring_boot_security.entity.UserEntity;
import sesac.sesac_spring_boot_security.security.TokenProvider;
import sesac.sesac_spring_boot_security.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) { // <?> 는 어떤 타입이든 허용함을 의미
        try {
            // 1. body 에 전달된 userDTO 정보를 이용해서 userEntity 인스턴스 생성
            UserEntity user = UserEntity.builder()
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    // .password(userDTO.getPassword()) // 암호화 하기 전
                    .password(passwordEncoder.encode(userDTO.getPassword())) // 암호화 후
                    .build();

            // 2. service 의 create 를 이용해서 db에 새로운 user entity 저장
            UserEntity registeredUser = userService.create(user);

            // 3. 회원가입 정보 DB 저장 후 client 로 보내줄 정보 생성(password 제외하고 필드 설정)
            UserDTO responseUserDTO = UserDTO.builder()
                    .email(registeredUser.getEmail())
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .build();

            // 4. 클라이언트에게 응답
            /*
            * ResponseEntity:  HTTP 응답을 직접 구성할 수 있는 Spring 클래스
            * - 메소드별 적절한 메소드 전달: ok 200, notFound 404, .. 등
            * - body() 에는 client 에 응답할 정보 전달
            *  */
            return ResponseEntity.ok().body(responseUserDTO); // status code 200, responseUserDTO 객체를 클라이언트에게 전달한다.
        } catch (Exception e) {
            // 회원가입 중 에러가 발생하면, 에러 메시지를 담은 DTO 를 만들어 클라이언트에 전달
            // -> ResponseDTO 의 error() 메소드 이용해서 클라이언트에게 에러 전달
            ResponseDTO<?> responseUserDTO = ResponseDTO.builder().error(e.getMessage()).build(); // data 정보는 사용하지 않기 때문에 generic 에 ? 전달
            return ResponseEntity.badRequest().body(responseUserDTO); // badRequest(): status code 400, body: error 객체가 담겨있는 객체
        }
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        UserEntity user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword(), passwordEncoder);

        // user 가 not null 인 경우 > 로그인 가능
        // user 가 null 인 경우 > 다양한 이유로.. 로그인 불가! (email 일치하는 user 를 못찾았거나, 비밀번호를 틀렸거나)
        if (user!=null){
            final String token = tokenProvider.create(user); // 토큰 생성

            // 클라이언트에게 응답을 위해 user dto 생성
            final UserDTO responseUserDTO = UserDTO.builder()
                                                   .email(user.getEmail())
                                                   .id(user.getId()) // 찾은 entity 에서 id, email 정보 저장
                                                   .token(token) // token 정보 저장
                                                   .build();

            return ResponseEntity.ok().body(responseUserDTO);
        }else{
            ResponseDTO<?> responseDTO = ResponseDTO.builder().error("Login failed.").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
