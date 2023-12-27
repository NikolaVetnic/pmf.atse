package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;

import java.util.List;
import java.util.Set;

public interface ParentService {

    ParentEntity getParent(String username);
    List<Set<GradeCardEntity>> getGradeCards(String username);
    List<GradeCardEntity> getGradeCardsForSubject(String username, Integer subjectId);
    ParentEntity updateParent(Integer id, ParentRegisterDto parentDto, BindingResult result);
    ParentEntity connectStudentWithParent(Integer studentId, Integer parentId);
    void deleteParent(Integer id);
}
