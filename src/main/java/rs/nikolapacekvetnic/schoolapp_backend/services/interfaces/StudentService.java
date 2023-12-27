package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;

import java.util.List;
import java.util.Set;

public interface StudentService {

    StudentEntity getStudent(String username);
    Set<GradeCardEntity> getGradeCards(String username);
    List<GradeCardEntity> getGradeCardsForSubject(String username, Integer subjectId);
    StudentEntity updateStudent(Integer id, StudentRegisterDto studentDto, BindingResult result);
    void deleteStudent(Integer id);
}
