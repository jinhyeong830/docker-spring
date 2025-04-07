package sesac.sesac_spring_boot_security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class UserDTO {
    private String token;
    private String email;
    private String username;
    private String password;
    private Long id;
}
