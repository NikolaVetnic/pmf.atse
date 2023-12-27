package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;

public interface SchoolClassService {
    SchoolClassEntity addNewSchoolClass(SchoolClassRegisterDto schoolClassDTO, BindingResult result);
    StudentEntity connectStudentWithClass(Integer studentId, Integer schoolClassId);
}
