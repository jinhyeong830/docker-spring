package sesac.sesac_spring_boot_security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sesac.sesac_spring_boot_security.entity.TodoEntity;
import sesac.sesac_spring_boot_security.repository.TodoRepository;

import java.util.List;
import java.util.Optional;

/*
* @TODO
*   userid 관련 조회하는 부분 > 가장 효율적인 로직으로 변경할 필요 있음.
*   user 자체를 받아서 조회할지, id가 String, Long 타입일지 등등.
*
* */
@Slf4j
// - Simple Logging Facade for Java
// - 로그 라이브러리
// - 용도에 따라 info, degub, warn, error 를 나누어서 로깅 가능
// - 로깅을 하는 클래스에 어노테이션을 붙이면 됨
@Service
// - 내부에 @Controller 어노테이션 가짐
// - @component 어노테이션과 비교했을 때 기능적 차이 없음
// - 해당 클래스가 스프링 컴포넌트이며, 기능적으로 비즈니스 로직임을 수행하는 서비스 레이어임을 알리는 어노테이션

public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public String testService(){
        TodoEntity entity = TodoEntity.builder().title("sample todo1").build();

        todoRepository.save(entity);

        TodoEntity savedEntity = todoRepository.findById(entity.getId()).get(); // optional 이라서 적어도 isPresent() 로 체크하는게 맞음.. >> 추후 반영

        return savedEntity.getTitle();
    }

    // create 투두
    public List<TodoEntity> create(final TodoEntity entity){
        validate(entity);
        todoRepository.save(entity);

        log.info("Entity id: {} is saved.", entity.getId());

        return todoRepository.findByUser_Id(entity.getUser().getId());
    }

    // read 투두
    public List<TodoEntity> retrieve(final String userId) {
        // 반드시 숫자형 문자열로 들어와야 함. 숫자형이 아닐 경우에 변환 과정에서 exception
        return todoRepository.findByUser_Id(Long.parseLong(userId));
    }

    // update 투두
    public List<TodoEntity> update(final TodoEntity entity){
        // Optional 클래스: null 일 수 있는 객체를 감싸는 래퍼 클래스 (안전한 null 처리 가능)
        // - isPresent() 메서드: Optional 한 객체가 값을 포함하는지를 확인 (값이 있으면 true, null 이면 false)
        // - get() 메서드: Optional 객체 내부에 저장된 값 반환

        Optional<TodoEntity> optionalTodo = todoRepository.findById(entity.getId());

        if(optionalTodo.isPresent()) {
            TodoEntity todo = optionalTodo.get();
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            todoRepository.save(todo);
        }
        return retrieve(entity.getUser().getId().toString());
    }

    public List<TodoEntity> delete(final TodoEntity entity){
        try {
            Optional<TodoEntity> original = todoRepository.findById(entity.getId());
            if(original.isPresent()){
                TodoEntity todo = original.get();
                // Long 끼리 비교..
                if(todo.getUser().getId().equals(entity.getUser().getId())){
                    todoRepository.delete(todo);
                }else{
                    log.warn("Delete failed. User {} is not the owner of the Todo {}", entity.getUser().getId(), entity.getId());
                    throw new RuntimeException("You don't have permission to delete this todo");
                }
            } else{
                log.warn("Todo not found with id: {}", entity.getId());
                throw new RuntimeException("Todo not found");
            }
        } catch (Exception e){
            log.error("error deleting entity id: {}", entity.getId(), e);
            throw new RuntimeException("error deleting entity" + entity.getId());
        }
        return retrieve(entity.getUser().getId().toString());
    }

    // 해당 서비스내에서 사용할 예정이기 때문에 private 처리
    private void validate(final TodoEntity entity){
        if(entity == null){
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(entity.getUser() == null){
            log.warn("Unknown user");
            throw new RuntimeException("Unknown user.");

        }
    }
}
