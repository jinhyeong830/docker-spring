package sesac.sesac_spring_boot_security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sesac.sesac_spring_boot_security.entity.TodoEntity;

import java.util.List;

// 관계 맺었으니 아래 repository도 수정해야함.
///////// 여기부터!!!!!!!!!!!
@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    // 연관관계로 맺은 user 객체의 id 를 조회하는 jpa,
    // SELECT * FROM 투두 WHERE user_id = ?;
    List<TodoEntity> findByUser_Id(Long userId);
}
