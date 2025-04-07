package sesac.sesac_spring_boot_security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = "email")}) // unique key 설정
// @Table 어노테이션에서 name 을 지정하지 않으면?
// -> @Entity 의 이름을 테이블 이름으로 간주
// -> @Entity 에 이름을 지정하지 않는 경우 클래스 이름을 테이블 이름으로 간주
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //
    @Column(name = "id", updatable = false) // 실제 컬럼명 id, 수정 가능 여부 false
    private Long id;

    @Column(name = "username", nullable = false) // 실제 컬럼명 username, null 허용 안함
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Builder.Default // builder 패턴 사용시? 초기화된 값 덮어쓰기 하기 위해서
    // 투두 Entity 와 양방향 매핑을 위해 user:투두 = 1: N 이므로 List 타입으로 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoEntity> todos = new ArrayList<>();

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<TodoEntity> todos;
}
