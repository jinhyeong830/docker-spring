package sesac.sesac_spring_boot_security.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@Entity
@Data
@Table(name="todo")
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="done", nullable = false)
    private boolean done;

    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩, 투두 entity 조회할 때마다 userEntity 를 조회하지 않고, 필요할 때만 userEntity 조회하도록
    @JoinColumn(name = "userId", nullable = false)
//    @Column(name = "userId") // 컬럼 이름 강제로 camelCase 로 설정? >> err
    UserEntity user;
}