package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;

import java.util.List;

public interface TeacherService {

    TeacherEntity getTeacher(String username);
    List<GradeCardEntity> getTeacherGradeCards(TeacherEntity teacher);
    TeacherEntity updateTeacher(Integer id, TeacherRegisterDto teacherDTO, BindingResult result);
    void deleteTeacher(Integer id);
}
