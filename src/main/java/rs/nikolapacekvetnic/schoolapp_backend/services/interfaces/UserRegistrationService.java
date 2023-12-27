package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;

public interface UserRegistrationService {

    AdminEntity addNewAdmin(AdminRegisterDto adminRegisterDto, BindingResult result);
    TeacherEntity addNewTeacher(TeacherRegisterDto teacherRegisterDto, BindingResult result);
    ParentEntity addNewParent(ParentRegisterDto parentRegisterDto, BindingResult result);
    StudentEntity addNewStudent(StudentRegisterDto studentRegisterDto, BindingResult result);
}
